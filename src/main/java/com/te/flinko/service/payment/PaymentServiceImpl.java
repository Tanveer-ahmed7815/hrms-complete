package com.te.flinko.service.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SignatureException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.te.flinko.dto.admin.PlanDTO;
import com.te.flinko.dto.employee.MailDto;
import com.te.flinko.dto.payment.PaymentRequestDTO;
import com.te.flinko.dto.payment.RazorpayResponseDTO;
import com.te.flinko.entity.account.mongo.CurrencyDetails;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.superadmin.PaymentDetails;
import com.te.flinko.entity.superadmin.PlanDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.exception.PaymentFailedException;
import com.te.flinko.repository.account.mongo.CurrencyConvertRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.superadmin.mongo.PlanDetailsRepository;
import com.te.flinko.service.mail.employee.EmailService;
import com.te.flinko.service.notification.employee.InAppNotificationServiceImpl;
import com.te.flinko.util.CacheStore;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	@Value("${algorithm}")
	private String hmacSha256Alorithm;

	private String generatedSignature;

	@Value("${keyid}")
	private String keyId;

	@Value("${keysecret}")
	private String keySecret;

	private final CacheStore<PlanDTO> cacheStorePlanDTO;

	private final PlanDetailsRepository planDetailsRepository;

	private final EmailService emailService;

	private final CompanyInfoRepository companyInfoRepository;

	private final CurrencyConvertRepository currencyConvertRepository;

	private final InAppNotificationServiceImpl inAppNotificationServiceImpl;

	private PlanDTO planDTO;

	Long extraEmpl = 0l;

	@Override
	public PlanDTO calculateAmount(String termonalId, PlanDTO planDto, HttpServletRequest request) {
		Locale locale = new Locale("en", "IN");
		String currencyCode = Currency.getInstance(locale).getCurrencyCode();
		planDTO = planDto;

		Map<String, List<CurrencyDetails>> currency = currencyConvertRepository.findAll().get(0).getCurrency();

		CurrencyDetails currencyDetails = currency.entrySet().stream().skip(currency.size() - 1).findFirst()
				.orElseThrow().getValue().stream().filter(v -> v.getCurrencyCode().equalsIgnoreCase(currencyCode))
				.findFirst().orElseThrow();

		return Optional.ofNullable(planDTO)
				.map(dto -> planDetailsRepository.findByPlanName(planDTO.getPlanName()).map(plan -> {
					CompanyInfo companyInfo = companyInfoRepository.findById(planDTO.getCompanyId())
							.orElseThrow(() -> new DataNotFoundException("Company Details Not Found"));

					if (companyInfo.getNoOfEmp() != null)
						extraEmpl = companyInfo.getNoOfEmp() - plan.getNoOfEmp();

					BigDecimal extraEmpAmt = Optional.ofNullable(extraEmpl > 0).filter(m -> m)
							.map(money -> plan.getAdditionalCostPerEmp().multiply(BigDecimal.valueOf(extraEmpl)))
							.orElse(BigDecimal.ZERO);
					BeanUtils.copyProperties(plan, planDTO);

					planDTO.setMaxTotalAmount(plan.getDuration()
							.multiply(plan.getAmountPerMonth().add(extraEmpAmt).multiply(BigDecimal.valueOf(100)))
							.multiply(currencyDetails.getAmountDifference()).setScale(2, RoundingMode.HALF_DOWN));
					planDTO.setMinTotalAmount(plan.getDuration().multiply(plan.getAmountPerMonth().add(extraEmpAmt))
							.multiply(currencyDetails.getAmountDifference()).setScale(2, RoundingMode.HALF_DOWN));
					planDTO.setAmountPerMonth(planDTO.getAmountPerMonth()
							.multiply(currencyDetails.getAmountDifference()).setScale(2, RoundingMode.HALF_DOWN));
					planDTO.setAdditionalCostPerEmp(planDTO.getAdditionalCostPerEmp()
							.multiply(currencyDetails.getAmountDifference()).setScale(2, RoundingMode.HALF_DOWN));
					planDTO.setCurrencyCode(currencyCode);
					planDTO.setCurrencySymbol(currencyDetails.getCurrencySymbol());
					return planDTO;
				}).orElseThrow(() -> new DataNotFoundException("Plan Details Not Found")))
				.orElseThrow(() -> new DataNotFoundException("Something Went Wrong"));
	}

	@Override
	public String getOrderId(PaymentRequestDTO paymentRequestDTO) {

		Order order = null;
		String id = null;
		try {

			RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", paymentRequestDTO.getAmount()); // amount in the smallest currency unit
			orderRequest.put("currency", paymentRequestDTO.getCurrency());

			order = razorpayClient.Orders.create(orderRequest);

			JSONObject jsonObject = new JSONObject(String.valueOf(order));
			id = jsonObject.getString("id");
		} catch (RazorpayException e) {
			throw new PaymentFailedException(e.getMessage());
		}
		return id;

	}// End of getOrderId method.

	@Transactional
	@Override
	public boolean verifySignature(RazorpayResponseDTO razorpayResponseDTO, HttpServletRequest request) {
		try {
			Locale locale = new Locale("en", "IN");
			String currencyCode = Currency.getInstance(locale).getCurrencyCode();
			generatedSignature = calculateRFC2104HMAC(
					razorpayResponseDTO.getRazorpayOrderId() + "|" + razorpayResponseDTO.getRazorpayPaymentId(),
					keySecret);
			return Optional.ofNullable(generatedSignature.equals(razorpayResponseDTO.getRazorpaySignature()))
					.filter(p -> p)
					.map(x -> companyInfoRepository.findByCompanyId(razorpayResponseDTO.getCompanyId()).map(cm -> {
						PlanDetails planDetails = planDetailsRepository
								.findByPlanName(razorpayResponseDTO.getPlanName())
								.orElseThrow(() -> new PaymentFailedException("Plan Not Found"));
						cm.setIsActive(Boolean.TRUE);
						String generateCompanyCode = generateCompanyCode(cm.getCompanyName());
						String companyCode = cm.getCompanyCode();
						cm.setCompanyCode(companyCode == null ? generateCompanyCode : companyCode);
						cm.getPaymentDetailsList()
								.add(PaymentDetails.builder().planDetails(planDetails).companyInfo(cm)
										.amountPayed(razorpayResponseDTO.getTotalAmount())
										.rezorPayOrderId(razorpayResponseDTO.getRazorpayOrderId())
										.rezorPayPaymentId(razorpayResponseDTO.getRazorpayPaymentId())
										.modeOfPayment("Razor Pay").rezorPayCurrencyCode(currencyCode).build());
						if (companyCode == null) {
							sendMail(cm.getEmployeePersonalInfoList().get(0).getEmployeeOfficialInfo()
									.getOfficialEmailId(), razorpayResponseDTO.getUrl(), cm.getCompanyCode());
						}
						inAppNotificationServiceImpl
								.saveCompanyNotification(
										cm.getCompanyName() + " Company is subscribed to "
												+ razorpayResponseDTO.getPlanName() + " plan",
										razorpayResponseDTO.getCompanyId());
						return true;
					}).orElseThrow(() -> new PaymentFailedException("Company Does Not Exist"))).orElse(Boolean.FALSE);
		} catch (SignatureException e) {
			throw new PaymentFailedException(e.getMessage());
		}
	}// End of verifySignature method.

	public String calculateRFC2104HMAC(String data, String secret) throws java.security.SignatureException {
		String result;
		try {

			// get an hmac_sha256 key from the raw secret bytes
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), hmacSha256Alorithm);

			// get an hmac_sha256 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(hmacSha256Alorithm);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			result = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}// End of calculateRFC2104HMAC method

	private String generateCompanyCode(String companyName) {
		int uniqueValue = ThreadLocalRandom.current().nextInt(1, 1000);
		StringBuilder x = new StringBuilder("");
		String[] split = companyName.split(" ");
		StringBuilder y = new StringBuilder("");
		for (String str : split) {
			y.append(str.substring(0, 1).toUpperCase().concat(str.substring(1)));
		}
		for (char string2 : y.toString().toCharArray()) {
			if (string2 >= 65 && string2 <= 91)
				x.append(string2);
		}
		String companyCode = "" + x.toString().toUpperCase() + LocalDate.now().getYear()
				+ String.format("%03d", uniqueValue);
		if (!companyInfoRepository.findByCompanyCodeIgnoreCase(companyCode).isEmpty()) {
			generateCompanyCode(companyName);
		}
		return companyCode;
	}

	private void sendMail(String mailId, String url, String companyCode) {
		if (mailId != null) {
			MailDto mailDto = new MailDto();
			mailDto.setTo(mailId);
			mailDto.setSubject("Welcome to Flinko.app");
			mailDto.setBody("<!DOCTYPE html>\r\n" + "<html lang=\"en\">\r\n" + "<head>\r\n"
					+ "    <meta charset=\"UTF-8\">\r\n"
					+ "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
					+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
					+ "    <title>Document</title>\r\n" + "    <style>\r\n" + "        a{\r\n"
					+ "            text-decoration: none !important;\r\n" + "            padding: 10px;\r\n"
					+ "            color: white !important;\r\n" + "            display: flex;\r\n"
					+ "            background-color:#1181b2 !important;\r\n"
					+ "            justify-content: center;\r\n" + "            align-items: center;\r\n"
					+ "            width: 100px;\r\n" + "            border: 1px sloid black;\r\n"
					+ "            border-radius: 5px;\r\n" + "            font-size: 18px;\r\n" + "        }\r\n"
					+ "    </style>\r\n" + "</head>\r\n" + "<body>\r\n" + "    <div>\r\n"
					+ "    <p>We are happy to have you on board! <br/> \r\n"
					+ "    <br/> Need to enter Company Code, Employee Id and Password, while logging in. \r\n"
					+ "    <br/>Here is your Company Code : " + companyCode + "<b></b> \r\n"
					+ "    <br/>Kindly use your Employee Id and Password to login.<br/> \r\n" + "    <br/> <a href="
					+ url + ">Get Started</a>\r\n" + "    <br/> Thanks, <br/> <br/> Team Flinko.app Team </p>\r\n"
					+ "</div>\r\n" + "</body>\r\n" + "</html>");
			emailService.sendMailWithLink(mailDto);
		}
	}
}
