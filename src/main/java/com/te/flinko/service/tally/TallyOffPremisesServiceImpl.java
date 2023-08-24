package com.te.flinko.service.tally;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.te.flinko.dto.tally.OrderItemDTO;
import com.te.flinko.dto.tally.PayrollDetailsDTO;
import com.te.flinko.dto.tally.TallyDetailsDTO;
import com.te.flinko.entity.account.CompanyPurchaseOrder;
import com.te.flinko.entity.account.CompanySalesOrder;
import com.te.flinko.entity.account.PurchaseOrderItems;
import com.te.flinko.entity.account.SalesOrderItems;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admin.CompanyStockGroup;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.exception.hr.CompanyNotFoundException;
import com.te.flinko.exception.tally.TallySaasException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admin.CompanyStockGroupRepository;
import com.te.flinko.repository.admindept.CompanyPurchaseOrderRepository;
import com.te.flinko.repository.admindept.CompanySalesOrderRepository;
import com.te.flinko.repository.admindept.PurchaseOrderItemsRepository;
import com.te.flinko.repository.admindept.SalesOrderItemsRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.EmployeeSalaryDetailsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TallyOffPremisesServiceImpl implements TallyOffPremisesService {
	private static final String AMOUNT2 = "AMOUNT";

	@Autowired
	private CompanyInfoRepository companyInfoRepository;

	@Autowired
	private PurchaseOrderItemsRepository purchaseOrderItemsRepository;

	@Autowired
	private SalesOrderItemsRepository salesOrderItemsRepository;

	@Autowired
	private CompanyStockGroupRepository companyStockGroupRepository;

	@Autowired
	private CompanyPurchaseOrderRepository companyPurchaseOrderRepository;

	@Autowired
	private CompanySalesOrderRepository companySalesOrderRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Autowired
	private EmployeeSalaryDetailsRepository employeeSalaryDetailsRepository;

	@Transactional
	@Override
	public TallyDetailsDTO tallyDetails(MultipartFile master, MultipartFile transaction, String flag, Long companyId) {
		
		log.info("tallyDetails service method of TallyOffPremisesService is called");

		CompanyInfo companyInfo = companyInfoRepository.findById(companyId)
				.orElseThrow(() -> new CompanyNotFoundException("company not found"));

		List<OrderItemDTO> savePurchase = savePurchase(master, transaction, companyInfo);
		//
		List<OrderItemDTO> saveSales = saveSales(master, transaction, companyInfo);
		//
		List<OrderItemDTO> purchaseAndSales = new ArrayList<>();
		//
		purchaseAndSales.addAll(savePurchase);
		//
		purchaseAndSales.addAll(saveSales);
		//
		List<PayrollDetailsDTO> savePayroll = savePayroll(master, transaction, flag, companyInfo);
		//
		if (savePayroll == null) {
			return null;
		}

		TallyDetailsDTO tallyDetailsDTOs = new TallyDetailsDTO();

		tallyDetailsDTOs.setOrders(purchaseAndSales);

		tallyDetailsDTOs.setSalary(savePayroll);
		
		log.info("return from tallyDetails service method of TallyOffPremisesService");

		return tallyDetailsDTOs;
	}

	public List<OrderItemDTO> savePurchase(MultipartFile master, MultipartFile transaction, CompanyInfo companyInfo) {

		Map<String, String> listOfItemByGroup = stockItemByStockGroup(master, companyInfo.getCompanyName());

		List<OrderItemDTO> savePurchaseDetails = savePurchaseDetails(transaction, companyInfo.getCompanyName());

		List<OrderItemDTO> addPurchaseDetails = new ArrayList<>();
		

		for (OrderItemDTO orderItemDTO : savePurchaseDetails) {

			Boolean skip = false;

			CompanyPurchaseOrder companyPurchaseOrder = new CompanyPurchaseOrder();

			for (OrderItemDTO orderItemDTO2 : addPurchaseDetails) {
				
				if(orderItemDTO2.getLedgerName().equalsIgnoreCase(orderItemDTO.getLedgerName() + " (" + listOfItemByGroup.get(orderItemDTO.getProductName()) + ")") && orderItemDTO2.getProductName().equalsIgnoreCase(orderItemDTO.getProductName())){
					
				

				List<CompanyPurchaseOrder> findBySubject = companyPurchaseOrderRepository
						.findBySubjectAndCompanyInfoCompanyId(orderItemDTO.getLedgerName() + " (" + listOfItemByGroup.get(orderItemDTO.getProductName()) + ")",companyInfo.getCompanyId());

				if (!findBySubject.isEmpty()) {
					
					
					
					double subtotal = findBySubject.get(0).getSubTotal().doubleValue()
							+ orderItemDTO.getAmount().doubleValue();

					findBySubject.get(0).setSubTotal(BigDecimal.valueOf(subtotal).setScale(2, RoundingMode.HALF_UP));

					double taxTotal = findBySubject.get(0).getTaxTotal().doubleValue()
							+ orderItemDTO.getTaxTotal().doubleValue();

					findBySubject.get(0).setTaxTotal(BigDecimal.valueOf(taxTotal).setScale(2, RoundingMode.HALF_UP));

					double totalPayableAmount = findBySubject.get(0).getTotalPayableAmount().doubleValue()
							+ orderItemDTO.getTotalPayableAmount().doubleValue();

					findBySubject.get(0).setTotalPayableAmount(
							BigDecimal.valueOf(totalPayableAmount).setScale(2, RoundingMode.HALF_UP));

					PurchaseOrderItems stockItem = purchaseOrderItemsRepository
							.findByProductNameAndCompanyPurchaseOrderPurchaseOrderId(orderItemDTO.getProductName(),
									orderItemDTO2.getPurchaseOrderId());

					if (stockItem != null) {
						orderItemDTO.setAmount(orderItemDTO.getAmount().add(stockItem.getAmount()));
						orderItemDTO.setTax(orderItemDTO.getTax().add(stockItem.getTax()));
						orderItemDTO.setQuantity(orderItemDTO.getQuantity() + stockItem.getQuantity());

						stockItem.setAmount(orderItemDTO.getAmount());
						stockItem.setQuantity(orderItemDTO.getQuantity());
						stockItem.setTax(orderItemDTO.getTax());

					} else {

						PurchaseOrderItems purchaseOrderItems = new PurchaseOrderItems();

						BeanUtils.copyProperties(orderItemDTO, purchaseOrderItems);

						purchaseOrderItems.setCompanyPurchaseOrder(findBySubject.get(0));

						purchaseOrderItemsRepository.save(purchaseOrderItems);

					}
					skip = true;

					break;

				}

				}
				
			}

			

			if (Boolean.TRUE.equals(skip) || !listOfItemByGroup.containsKey(orderItemDTO.getProductName())) {
				continue;
			} else {
				Object value = listOfItemByGroup.get(orderItemDTO.getProductName());
				orderItemDTO.setStockGroup((String) value);
			}

			orderItemDTO.setLedgerName(orderItemDTO.getLedgerName() + " (" + orderItemDTO.getStockGroup() + ")");

			companyPurchaseOrder.setSubject(orderItemDTO.getLedgerName());

			CompanyStockGroup findByStockGroupName = companyStockGroupRepository
					.findByStockGroupNameAndCompanyInfoCompanyId(orderItemDTO.getStockGroup(),companyInfo.getCompanyId());

			if (findByStockGroupName == null) {
				CompanyStockGroup companyStockGroup = new CompanyStockGroup();
				companyStockGroup.setStockGroupName(orderItemDTO.getStockGroup());
				companyStockGroup.setCompanyInfo(companyInfo);
				findByStockGroupName = companyStockGroupRepository.save(companyStockGroup);

			}

			companyPurchaseOrder.setCompanyStockGroup(findByStockGroupName);

			List<CompanyPurchaseOrder> findBySubject = companyPurchaseOrderRepository
					.findBySubjectAndCompanyInfoCompanyId(companyPurchaseOrder.getSubject(),companyInfo.getCompanyId());

			CompanyPurchaseOrder savedCompanyPurchaseOrder;

			if (findBySubject.isEmpty()) {
				companyPurchaseOrder.setCompanyInfo(companyInfo);
				companyPurchaseOrder.setSubTotal(orderItemDTO.getAmount());

				companyPurchaseOrder.setTaxTotal(orderItemDTO.getTaxTotal());

				companyPurchaseOrder.setTotalPayableAmount(orderItemDTO.getTotalPayableAmount());
				companyPurchaseOrder.setPurchaseType("Indoor");
				companyPurchaseOrder.setType("OTHERS");
				savedCompanyPurchaseOrder = companyPurchaseOrderRepository.save(companyPurchaseOrder);
			}
			else if(addPurchaseDetails.stream().filter(x->x.getLedgerName().equals(orderItemDTO.getLedgerName())).findFirst().isPresent()){
				 
				savedCompanyPurchaseOrder = findBySubject.get(0);

				savedCompanyPurchaseOrder.setSubTotal(orderItemDTO.getAmount().add(savedCompanyPurchaseOrder.getSubTotal()));

				savedCompanyPurchaseOrder.setTaxTotal(orderItemDTO.getTaxTotal().add(savedCompanyPurchaseOrder.getTaxTotal()));

				savedCompanyPurchaseOrder.setTotalPayableAmount(orderItemDTO.getTotalPayableAmount().add(savedCompanyPurchaseOrder.getTotalPayableAmount()));
				}
			else {
				
				

				savedCompanyPurchaseOrder = findBySubject.get(0);

				savedCompanyPurchaseOrder.setSubTotal(orderItemDTO.getAmount());

				savedCompanyPurchaseOrder.setTaxTotal(orderItemDTO.getTaxTotal());

				savedCompanyPurchaseOrder.setTotalPayableAmount(orderItemDTO.getTotalPayableAmount());

			}
			
			addPurchaseDetails.add(orderItemDTO);

			PurchaseOrderItems purchaseOrderItems = new PurchaseOrderItems();

			BeanUtils.copyProperties(orderItemDTO, purchaseOrderItems);

			purchaseOrderItems.setCompanyPurchaseOrder(savedCompanyPurchaseOrder);

			PurchaseOrderItems stockItem = purchaseOrderItemsRepository
					.findByProductNameAndCompanyPurchaseOrderPurchaseOrderId(purchaseOrderItems.getProductName(),
							purchaseOrderItems.getCompanyPurchaseOrder().getPurchaseOrderId());

			if (stockItem == null) {

				purchaseOrderItemsRepository.save(purchaseOrderItems);
				orderItemDTO.setPurchaseOrderId(purchaseOrderItems.getCompanyPurchaseOrder().getPurchaseOrderId());
			} else {
				stockItem.setAmount(purchaseOrderItems.getAmount());
				stockItem.setQuantity(purchaseOrderItems.getQuantity());
				stockItem.setTax(purchaseOrderItems.getTax());
				orderItemDTO.setPurchaseOrderId(purchaseOrderItems.getCompanyPurchaseOrder().getPurchaseOrderId());
			}

		}

		return savePurchaseDetails;
	}
	
	public Map<String, String> stockItemByStockGroup(MultipartFile master, String companyName) {

		Map<String, String> listOfItemByGroup = new HashMap<>();

		String content = null;
		try {
			content = new String(master.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (content == null) {
			throw new TallySaasException("master xml file is empty");
		}

		content = content.replace("" + (char) 0, "");

		content = content.replace("&#4;", ";");

		content = content.replace("" + (char) 5, ";");

		int indexOf = content.indexOf("<ENVELOPE");

		if (indexOf == -1) {
			throw new TallySaasException("Wrong xml file of master or transaction uploaded");
		}

		content = content.substring(indexOf);

		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(content)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		if (doc == null) {
			throw new TallySaasException("master file is null");
		}

		String companyCheck = doc.getElementsByTagName("SVCURRENTCOMPANY").item(0).getTextContent();

		if (!companyCheck.equals(companyName)) {
			throw new TallySaasException("Please upload files of " + companyName + " company");
		}

		NodeList voucherXML = doc.getElementsByTagName("VOUCHER");

		if (voucherXML.getLength() > 0) {
			throw new TallySaasException("Wrong xml file of master or transaction uploaded");
		}

		NodeList itemAndGroupXML = doc.getElementsByTagName("TALLYMESSAGE");

		for (int i = 0; i < itemAndGroupXML.getLength(); i++) {

			Element itemAndGroup = (Element) itemAndGroupXML.item(i);

			NodeList stockItemXML = itemAndGroup.getElementsByTagName("STOCKITEM");

			String itemName="";
			
			String groupName="";
			
			if (stockItemXML.item(0) != null) {
				 itemName = stockItemXML.item(0).getAttributes().getNamedItem("NAME").getNodeValue();

				 groupName = itemAndGroup.getElementsByTagName("PARENT").item(0).getTextContent();
			}



			if (stockItemXML.item(0) == null || groupName.equalsIgnoreCase("")) {
				continue;
			}

			listOfItemByGroup.put(itemName, groupName);
			

		}

		return listOfItemByGroup;
	}

	public List<OrderItemDTO> savePurchaseDetails(MultipartFile transaction, String companyName) {
		String content = null;
		try {
			content = new String(transaction.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		content = content.replace("" + (char) 0, "");

		content = content.replace("&#4;", ";");

		content = content.replace("" + (char) 5, ";");

		int indexOf = content.indexOf("<ENVELOPE");

		if (indexOf == -1) {
			throw new TallySaasException("Wrong xml file of master or transaction uploaded");
		}

		content = content.substring(indexOf);
		

		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(content)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		String companyCheck = doc.getElementsByTagName("SVCURRENTCOMPANY").item(0).getTextContent();
		
		if (doc==null) {
			throw new TallySaasException("No transaction found in Transaction XML file");
		}

		if (!companyCheck.equals(companyName)) {
			throw new TallySaasException("Please upload files of " + companyName + " company");
		}

		List<OrderItemDTO> finalPurchaseOrderItemDTOs = new ArrayList<>();
		

		
		NodeList elementsByTagName = doc.getElementsByTagName("VOUCHERTYPENAME");

		if (elementsByTagName.getLength() == 0) {
			throw new TallySaasException("No transaction found in Transaction XML file");
		}

		for (int voucherTypeNumber = 0; voucherTypeNumber < elementsByTagName.getLength(); ++voucherTypeNumber) {
			
			Node parentNode = null;

			Element element1 = (Element) elementsByTagName.item(voucherTypeNumber);

			if (element1.getTextContent().equalsIgnoreCase("Purchase")) {

				parentNode = element1.getParentNode();
			}
		

		if (parentNode == null  && voucherTypeNumber==elementsByTagName.getLength()-1) {
			return finalPurchaseOrderItemDTOs;
		}
		
		if(parentNode == null) {
			continue;
		}

		Element element = (Element) parentNode;

		String partyLedgerName = "";

		if (element.getElementsByTagName("PARTYLEDGERNAME").item(0) != null) {
			partyLedgerName = element.getElementsByTagName("PARTYLEDGERNAME").item(0).getTextContent();

		}

		NodeList attendanceXML = element.getElementsByTagName("ALLINVENTORYENTRIES.LIST");
		
		
		List<OrderItemDTO> purchaseOrderItemDTOs = new ArrayList<>();
		

		for (int i = 0; i < attendanceXML.getLength(); i++) {
			

			OrderItemDTO orderItemDTO = new OrderItemDTO();

			Element x = (Element) attendanceXML.item(i);

			String itemName = x.getElementsByTagName("STOCKITEMNAME").item(0).getTextContent();
			String quantity = x.getElementsByTagName("BILLEDQTY").item(0).getTextContent();
			String[] split = quantity.split(" ");
			String discount = "0";
			if (x.getElementsByTagName("DISCOUNT").item(0) != null) {

				discount = x.getElementsByTagName("DISCOUNT").item(0).getTextContent();
			}

			String amount = x.getElementsByTagName(AMOUNT2).item(0).getTextContent();

			String tax = "0";
			if (x.getElementsByTagName("DISCOUNT").item(0) != null) {

				tax = x.getElementsByTagName("VATTAXRATE").item(0).getTextContent();

			}
			

			orderItemDTO.setAmount(new BigDecimal(amount));
			orderItemDTO.setDiscount(new BigDecimal(discount));
			orderItemDTO.setProductName(itemName);
			orderItemDTO.setQuantity(Long.parseLong(split[1]));
			orderItemDTO.setTax(new BigDecimal(tax));
			orderItemDTO.setLedgerName(partyLedgerName);
			if ((new BigDecimal(amount)).compareTo(new BigDecimal("0")) < 0) {

				orderItemDTO.setAmount(new BigDecimal(amount).multiply(new BigDecimal("-1")));
			}

			purchaseOrderItemDTOs.add(orderItemDTO);

		}
		

		NodeList taxXML = element.getElementsByTagName("LEDGERENTRIES.LIST");

		Double cgstTotal = 0.0;


		Double taxTotal = 0.0;
		for (int i = 0; i < taxXML.getLength(); i++) {
			
			Element taxElement = (Element) taxXML.item(i);
			
			String cgstAmount = taxElement.getElementsByTagName("AMOUNT").item(0).getTextContent();
			cgstTotal = (new BigDecimal(cgstAmount)).doubleValue();
			if ((new BigDecimal(cgstAmount)).compareTo(new BigDecimal("0")) < 0) {

				cgstTotal = (new BigDecimal(cgstAmount).multiply(new BigDecimal("-1")).doubleValue());
				
				taxTotal=taxTotal+cgstTotal;
			}
			
			

			



		}
		

		Double totalPayableAmount = purchaseOrderItemDTOs.stream().mapToDouble(x -> x.getAmount().doubleValue()).sum();

		Double taxPercentage = (100 * taxTotal) / totalPayableAmount;


		final Double taxPercentageFinal = taxPercentage;

		purchaseOrderItemDTOs.forEach(x -> {
			x.setTaxTotal(BigDecimal
					.valueOf((x.getAmount().doubleValue() * taxPercentageFinal) / 100
							)
					.setScale(2, RoundingMode.HALF_UP));
			x.setTotalPayableAmount(BigDecimal.valueOf(x.getAmount().doubleValue() + x.getTaxTotal().doubleValue())
					.setScale(2, RoundingMode.HALF_UP));
		});
		
		finalPurchaseOrderItemDTOs.addAll(purchaseOrderItemDTOs);
		
		}

		return finalPurchaseOrderItemDTOs;
	}

	public List<OrderItemDTO> saveSales(MultipartFile master, MultipartFile transaction, CompanyInfo companyInfo) {
		Map<String, String> listOfItemByGroup = stockItemByStockGroup(master, companyInfo.getCompanyName());

		List<OrderItemDTO> saveSalesDetails = saveSalesDetails(transaction);
		
		List<OrderItemDTO> addSalesDetails = new ArrayList<>();

		for (OrderItemDTO orderItemDTO : saveSalesDetails) {

			Boolean skip = false;

			CompanySalesOrder companySalesOrder = new CompanySalesOrder();

			for (OrderItemDTO orderItemDTO2 : addSalesDetails) {
				if(orderItemDTO2.getLedgerName().equalsIgnoreCase(orderItemDTO.getLedgerName() + " (" + listOfItemByGroup.get(orderItemDTO.getProductName()) + ")") && orderItemDTO2.getProductName().equalsIgnoreCase(orderItemDTO.getProductName())){
				List<CompanySalesOrder> findBySubject = companySalesOrderRepository
						.findBySubjectAndCompanyInfoCompanyId(orderItemDTO.getLedgerName() + " (" + listOfItemByGroup.get(orderItemDTO.getProductName()) + ")",companyInfo.getCompanyId());

				if (!findBySubject.isEmpty()) {
					double subtotal = findBySubject.get(0).getSubTotal().doubleValue()
							+ orderItemDTO.getAmount().doubleValue();

					findBySubject.get(0).setSubTotal(BigDecimal.valueOf(subtotal).setScale(2, RoundingMode.HALF_UP));

					double taxTotal = findBySubject.get(0).getTaxTotal().doubleValue()
							+ orderItemDTO.getTaxTotal().doubleValue();

					findBySubject.get(0).setTaxTotal(BigDecimal.valueOf(taxTotal).setScale(2, RoundingMode.HALF_UP));

					double totalReceivableAmount = findBySubject.get(0).getTotalReceivableAmount().doubleValue()
							+ orderItemDTO.getTotalReceivableAmount().doubleValue();

					findBySubject.get(0).setTotalReceivableAmount(
							BigDecimal.valueOf(totalReceivableAmount).setScale(2, RoundingMode.HALF_UP));

					SalesOrderItems stockItem = salesOrderItemsRepository
							.findByProductNameAndCompanySalesOrderSalesOrderId(orderItemDTO.getProductName(),
									orderItemDTO2.getSalesOrderId());

					if (stockItem != null) {
						orderItemDTO.setAmount(orderItemDTO.getAmount().add(stockItem.getAmount()));
						orderItemDTO.setTax(orderItemDTO.getTax().add(stockItem.getTax()));
						orderItemDTO.setQuantity(orderItemDTO.getQuantity() + stockItem.getQuantity());

						stockItem.setAmount(orderItemDTO.getAmount());
						stockItem.setQuantity(orderItemDTO.getQuantity());
						stockItem.setTax(orderItemDTO.getTax());

					} else {

						SalesOrderItems salesOrderItems = new SalesOrderItems();

						BeanUtils.copyProperties(orderItemDTO, salesOrderItems);

						salesOrderItems.setCompanySalesOrder(findBySubject.get(0));

						salesOrderItemsRepository.save(salesOrderItems);

					}
					skip = true;

					break;

				}

				}
				
				}
			
			
			if (Boolean.TRUE.equals(skip) || !listOfItemByGroup.containsKey(orderItemDTO.getProductName())) {
				continue;
			} else {
				Object value = listOfItemByGroup.get(orderItemDTO.getProductName());
				orderItemDTO.setStockGroup((String) value);
			}

			orderItemDTO.setLedgerName(orderItemDTO.getLedgerName() + " (" + orderItemDTO.getStockGroup() + ")");

			companySalesOrder.setSubject(orderItemDTO.getLedgerName());

			CompanyStockGroup findByStockGroupName = companyStockGroupRepository
					.findByStockGroupNameAndCompanyInfoCompanyId(orderItemDTO.getStockGroup(),companyInfo.getCompanyId());

			if (findByStockGroupName == null) {
				CompanyStockGroup companyStockGroup = new CompanyStockGroup();
				companyStockGroup.setStockGroupName(orderItemDTO.getStockGroup());
				companyStockGroup.setCompanyInfo(companyInfo);
				findByStockGroupName = companyStockGroupRepository.save(companyStockGroup);

			}

			companySalesOrder.setCompanyStockGroup(findByStockGroupName);

			List<CompanySalesOrder> findBySubject = companySalesOrderRepository
					.findBySubjectAndCompanyInfoCompanyId(companySalesOrder.getSubject(),companyInfo.getCompanyId());

			CompanySalesOrder savedCompanySalesOrder;

			if (findBySubject.isEmpty()) {
				companySalesOrder.setCompanyInfo(companyInfo);
				companySalesOrder.setSubTotal(orderItemDTO.getAmount());

				companySalesOrder.setTaxTotal(orderItemDTO.getTaxTotal());
				companySalesOrder.setTotalReceivableAmount(orderItemDTO.getTotalReceivableAmount());
				companySalesOrder.setType("OTHERS");
				savedCompanySalesOrder = companySalesOrderRepository.save(companySalesOrder);
			}
			 else if(addSalesDetails.stream().filter(x->x.getLedgerName().equals(orderItemDTO.getLedgerName())).findFirst().isPresent()){
				 
					savedCompanySalesOrder = findBySubject.get(0);

					savedCompanySalesOrder.setSubTotal(orderItemDTO.getAmount().add(savedCompanySalesOrder.getSubTotal()));

					savedCompanySalesOrder.setTaxTotal(orderItemDTO.getTaxTotal().add(savedCompanySalesOrder.getTaxTotal()));

					savedCompanySalesOrder.setTotalReceivableAmount(orderItemDTO.getTotalReceivableAmount().add(savedCompanySalesOrder.getTotalReceivableAmount()));
				}
			else{

				savedCompanySalesOrder = findBySubject.get(0);

				savedCompanySalesOrder.setSubTotal(orderItemDTO.getAmount());

				savedCompanySalesOrder.setTaxTotal(orderItemDTO.getTaxTotal());

				savedCompanySalesOrder.setTotalReceivableAmount(orderItemDTO.getTotalReceivableAmount());
			}
			
			addSalesDetails.add(orderItemDTO);

			SalesOrderItems salesOrderItems = new SalesOrderItems();

			BeanUtils.copyProperties(orderItemDTO, salesOrderItems);

			salesOrderItems.setCompanySalesOrder(savedCompanySalesOrder);

			SalesOrderItems stockItem = salesOrderItemsRepository.findByProductNameAndCompanySalesOrderSalesOrderId(
					salesOrderItems.getProductName(), salesOrderItems.getCompanySalesOrder().getSalesOrderId());

			if (stockItem == null) {

				salesOrderItemsRepository.save(salesOrderItems);
				orderItemDTO.setSalesOrderId(salesOrderItems.getCompanySalesOrder().getSalesOrderId());
			} else {
				stockItem.setAmount(salesOrderItems.getAmount());
				stockItem.setQuantity(salesOrderItems.getQuantity());
				stockItem.setTax(salesOrderItems.getTax());
				orderItemDTO.setSalesOrderId(salesOrderItems.getCompanySalesOrder().getSalesOrderId());
			}

		}

		return saveSalesDetails;

	}

	public List<OrderItemDTO> saveSalesDetails(MultipartFile transaction) {
		String content = null;
		try {
			content = new String(transaction.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		content = content.replace("" + (char) 0, "");

		content = content.replace("&#4;", ";");
		content = content.replace("" + (char) 5, ";");

		int indexOf = content.indexOf("<ENVELOPE");

		content = content.substring(indexOf);

		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(content)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		List<OrderItemDTO> finalPurchaseOrderItemDTOs = new ArrayList<>();
		
		


		NodeList elementsByTagName = doc.getElementsByTagName("VOUCHERTYPENAME");

		for (int voucherTypeNumber = 0; voucherTypeNumber < elementsByTagName.getLength(); ++voucherTypeNumber) {

			Node parentNode = null;
			
			Element element1 = (Element) elementsByTagName.item(voucherTypeNumber);

			if (element1.getTextContent().equalsIgnoreCase("Sales")) {

				parentNode = element1.getParentNode();
			}
		

			if (parentNode == null  && voucherTypeNumber==elementsByTagName.getLength()-1) {
				return finalPurchaseOrderItemDTOs;
			}
			
			if(parentNode == null) {
				continue;
			}

		Element element = (Element) parentNode;

		String partyLedgerName = "";

		if (element.getElementsByTagName("PARTYLEDGERNAME").item(0) != null) {
			partyLedgerName = element.getElementsByTagName("PARTYLEDGERNAME").item(0).getTextContent();

		}

		NodeList attendanceXML = element.getElementsByTagName("ALLINVENTORYENTRIES.LIST");
		
		List<OrderItemDTO> purchaseOrderItemDTOs = new ArrayList<>();

		for (int i = 0; i < attendanceXML.getLength(); i++) {

			OrderItemDTO orderItemDTO = new OrderItemDTO();

			Element x = (Element) attendanceXML.item(i);

			String itemName = x.getElementsByTagName("STOCKITEMNAME").item(0).getTextContent();
			String quantity = x.getElementsByTagName("BILLEDQTY").item(0).getTextContent();
			String[] split = quantity.split(" ");
			String discount = "0";
			if (x.getElementsByTagName("DISCOUNT").item(0) != null) {

				discount = x.getElementsByTagName("DISCOUNT").item(0).getTextContent();
			}

			String amount = x.getElementsByTagName(AMOUNT2).item(0).getTextContent();

			String tax = "0";
			if (x.getElementsByTagName("DISCOUNT").item(0) != null) {

				tax = x.getElementsByTagName("VATTAXRATE").item(0).getTextContent();

			}

			orderItemDTO.setAmount(new BigDecimal(amount));
			orderItemDTO.setDiscount(new BigDecimal(discount));
			orderItemDTO.setProductName(itemName);
			orderItemDTO.setQuantity(Long.parseLong(split[1]));
			orderItemDTO.setTax(new BigDecimal(tax));
			orderItemDTO.setLedgerName(partyLedgerName);
			if ((new BigDecimal(amount)).compareTo(new BigDecimal("0")) < 0) {

				orderItemDTO.setAmount(new BigDecimal(amount).multiply(new BigDecimal("-1")));
			}

			purchaseOrderItemDTOs.add(orderItemDTO);

		}

		NodeList taxXML = element.getElementsByTagName("LEDGERENTRIES.LIST");

		Double cgstTotal = 0.0;

		Double sgstTotal = 0.0;
		
		Double taxTotal = 0.0;

		for (int i = 0; i < taxXML.getLength(); i++) {

			Element taxElement = (Element) taxXML.item(i);

			
			String cgstAmount = taxElement.getElementsByTagName("AMOUNT").item(0).getTextContent();
			cgstTotal = (new BigDecimal(cgstAmount)).doubleValue();
			if ((new BigDecimal(cgstAmount)).compareTo(new BigDecimal("0")) > 0) {

				
				taxTotal=taxTotal+cgstTotal;
			}


		}

		Double totalPayableAmount = purchaseOrderItemDTOs.stream().mapToDouble(x -> x.getAmount().doubleValue()).sum();

		Double taxPercentage = (100 * taxTotal) / totalPayableAmount;


		final Double taxPercentageFinal = taxPercentage;
		

		purchaseOrderItemDTOs.forEach(x -> {
			x.setTaxTotal(BigDecimal
					.valueOf((x.getAmount().doubleValue() * taxPercentageFinal) / 100
							)
					.setScale(2, RoundingMode.HALF_UP));
			x.setTotalReceivableAmount(BigDecimal.valueOf(x.getAmount().doubleValue() + x.getTaxTotal().doubleValue())
					.setScale(2, RoundingMode.HALF_UP));
		});
		
		finalPurchaseOrderItemDTOs.addAll(purchaseOrderItemDTOs);
		}

		return finalPurchaseOrderItemDTOs;
	}

	public List<PayrollDetailsDTO> savePayroll(MultipartFile master, MultipartFile transaction, String flag,
			CompanyInfo companyInfo) {
		log.info("savePayroll mryhod is called");
		List<PayrollDetailsDTO> savePayrollDetails = savePayrollDetails(master, transaction);
		List<EmployeeSalaryDetails> salaryList=new ArrayList<>();
		for (PayrollDetailsDTO payrollDetailsDTO : savePayrollDetails) {

			EmployeePersonalInfo employeePersonalInfo;

			Optional<List<EmployeePersonalInfo>> employeePersonalInfoList = employeePersonalInfoRepository
					.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(companyInfo.getCompanyId(),
							payrollDetailsDTO.getEmployeeId());

			if (!employeePersonalInfoList.isPresent() || employeePersonalInfoList.get().isEmpty()) {
				continue;
			} else {

				employeePersonalInfo = employeePersonalInfoList.get().get(0);
			}

			EmployeeSalaryDetails employeeSalaryDetails = new EmployeeSalaryDetails();

			Optional<EmployeeSalaryDetails> salaryOfEmployee = employeeSalaryDetailsRepository
					.findByEmployeePersonalInfoEmployeeInfoIdAndMonthAndYearAndCompanyInfoCompanyId(
							employeePersonalInfo.getEmployeeInfoId(), payrollDetailsDTO.getMonth(),
							payrollDetailsDTO.getYear(), companyInfo.getCompanyId());

			if (salaryOfEmployee.isPresent() && ((salaryOfEmployee.get().getCalculatedFrom() != null
					&& !salaryOfEmployee.get().getCalculatedFrom().equalsIgnoreCase("Tally")) || salaryOfEmployee.get().getCalculatedFrom() == null)) {
				if (flag != null && flag.equalsIgnoreCase("proceed")) {
					employeeSalaryDetails = salaryOfEmployee.get();
				} else if (flag != null && flag.equalsIgnoreCase("skip")) {
					continue;
				} else if (flag == null || flag.equalsIgnoreCase("null")) {
					return null;
				}

			} else if (salaryOfEmployee.isPresent() && (salaryOfEmployee.get().getCalculatedFrom() != null
					&& salaryOfEmployee.get().getCalculatedFrom().equalsIgnoreCase("Tally"))) {
				employeeSalaryDetails = salaryOfEmployee.get();
			}

			BeanUtils.copyProperties(payrollDetailsDTO, employeeSalaryDetails);
			employeeSalaryDetails.setEarning(payrollDetailsDTO.getEarning());
			employeeSalaryDetails.setDeduction(payrollDetailsDTO.getDeduction());

			employeeSalaryDetails.setEmployeePersonalInfo(employeePersonalInfo);
			employeeSalaryDetails.setCompanyInfo(companyInfo);
			employeeSalaryDetails.setCalculatedFrom("Tally");
			employeeSalaryDetails.setIsFinalized(false);
			employeeSalaryDetails.setIsPaid(false);
			employeeSalaryDetails.setIsPayslipGenerated(false);
			salaryList.add(employeeSalaryDetails);
		}
		salaryList.forEach(x->employeeSalaryDetailsRepository.save(x));
		return savePayrollDetails;
	}

	public List<PayrollDetailsDTO> savePayrollDetails(MultipartFile master, MultipartFile transaction) {
		
		log.info("savePayrollDetails method is called");

		String content = null;
		try {
			content = new String(master.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		content = content.replace("" + (char) 0, "");

		content = content.replace("&#4;", ";");
		content = content.replace("" + (char) 5, ";");

		int indexOf = content.indexOf("<ENVELOPE");

		content = content.substring(indexOf);

		Document doc1 = null;
		try {
			doc1 = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(content)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		 }

		Map<String, String> employee = new HashMap<>();

		NodeList employeeIdXML = doc1.getElementsByTagName("TALLYMESSAGE");

		for (int k = 0; k < employeeIdXML.getLength(); k++) {
			Element voucherListByDate = (Element) employeeIdXML.item(k);
			if (voucherListByDate.getElementsByTagName("EMPDISPLAYNAME").item(0) != null
					&& voucherListByDate.getElementsByTagName("MAILINGNAME").item(0) != null) {
				String employeeName = voucherListByDate.getElementsByTagName("EMPDISPLAYNAME").item(0).getTextContent();
				String employeeId = voucherListByDate.getElementsByTagName("MAILINGNAME").item(0).getTextContent();
				employee.put(employeeName, employeeId);
			}

		}

		String content1 = null;
		try {
			content1 = new String(transaction.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		content1 = content1.replace("" + (char) 0, "");

		content1 = content1.replace("&#4;", ";");

		content1 = content1.substring(indexOf);

		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(content1)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		List<PayrollDetailsDTO> payrollDetailsDTOs = new ArrayList<>();


		NodeList elementsByTagName = doc.getElementsByTagName("VOUCHERTYPENAME");

		for (int voucherTypeNumber = 0; voucherTypeNumber < elementsByTagName.getLength(); ++voucherTypeNumber) {

			Node parentNode = null;

			Element element1 = (Element) elementsByTagName.item(voucherTypeNumber);

			if (element1.getTextContent().equalsIgnoreCase("Payroll")) {

				parentNode = element1.getParentNode();
			}
			

			
			if (parentNode == null && voucherTypeNumber==elementsByTagName.getLength()-1) {
				return payrollDetailsDTOs;
			}
			
			if (parentNode == null) {
				continue;
			}

			Element voucherListByDate = (Element) parentNode;

			NodeList employeeNameXML = voucherListByDate.getElementsByTagName("EMPLOYEEENTRIES.LIST");
			String date = voucherListByDate.getElementsByTagName("FBTFROMDATE").item(0).getTextContent();
			for (int i = 0; i < employeeNameXML.getLength(); i++) {

				PayrollDetailsDTO payrollDetailsDTO = new PayrollDetailsDTO();
				if (employeeNameXML.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) employeeNameXML.item(i);
					String employeeName = el.getElementsByTagName("EMPLOYEENAME").item(0).getTextContent();
					String totalAmount = el.getElementsByTagName(AMOUNT2).item(0).getTextContent();
					NodeList payheadDetailsXML = el.getElementsByTagName("PAYHEADALLOCATIONS.LIST");

					payrollDetailsDTO.setEmployeeName(employeeName);
					payrollDetailsDTO.setYear(Integer.parseInt(date.substring(0, 4)));
					payrollDetailsDTO.setMonth(Integer.parseInt(date.substring(4, 6)));

					payrollDetailsDTO.setEmployeeId(employee.get(payrollDetailsDTO.getEmployeeName()));
					
					payrollDetailsDTO.setTotalSalary(new BigDecimal(totalAmount).multiply(new BigDecimal("-1")));


					for (int z = 0; z < payheadDetailsXML.getLength(); z++) {
						Map<String, String> deductionOrEarning = new HashMap<>();

						if (payheadDetailsXML.item(z).getNodeType() == Node.ELEMENT_NODE) {
							Element payhead = (Element) payheadDetailsXML.item(z);
							String payheadname = payhead.getElementsByTagName("PAYHEADNAME").item(0).getTextContent();
							String amount = payhead.getElementsByTagName(AMOUNT2).item(0).getTextContent();

							deductionOrEarning.put(payheadname, amount);

							if ((new BigDecimal(amount)).compareTo(new BigDecimal("0")) < 0) {
								Map<String, String> earning = new HashMap<>();

								if (payrollDetailsDTO.getEarning() != null) {
									earning.putAll(payrollDetailsDTO.getEarning());
								}

								amount = amount.substring(1, amount.length());

								earning.put(payheadname, amount);
								payrollDetailsDTO.setEarning(earning);
							} else {
								Map<String, String> deduction = new HashMap<>();

								if (payrollDetailsDTO.getDeduction() != null) {
									deduction.putAll(payrollDetailsDTO.getDeduction());
								}

								deduction.put(payheadname, amount);
								payrollDetailsDTO.setDeduction(deduction);
							}

						}
					}

				}
				payrollDetailsDTOs.add(payrollDetailsDTO);
			}


		}
		return payrollDetailsDTOs;
		}

		
}
