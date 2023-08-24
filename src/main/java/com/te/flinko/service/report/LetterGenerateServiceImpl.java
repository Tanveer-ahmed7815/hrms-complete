package com.te.flinko.service.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.te.flinko.dto.hr.EmployeeBasicDetailsDTO;
import com.te.flinko.dto.hr.LetterDetailsDTO;
import com.te.flinko.dto.project.ProjectEstimationDTO;
import com.te.flinko.dto.report.LetterDTO;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.mongo.EmployeeDocumentDetails;
import com.te.flinko.entity.employee.mongo.EmployeeLetterDetails;
import com.te.flinko.entity.employee.mongo.LetterDetails;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.entity.project.ProjectEstimationDetails;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.mongo.EmployeeDocumentDetailsRepository;
import com.te.flinko.repository.employee.mongo.EmployeeLetterDetailsRepository;
import com.te.flinko.util.FileToMultipartConverter;
import com.te.flinko.util.Generator;
import com.te.flinko.util.S3UploadFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LetterGenerateServiceImpl implements LetterGenerateService {

	private final Generator generator;

	private final S3UploadFile s3UploadFile;

	private final FileToMultipartConverter fileToMultipartConverter;

	private final EmployeeDocumentDetailsRepository documentDetailsRepository;

	private final CompanyInfoRepository companyInfoRepository;

	private final EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private final EmployeeLetterDetailsRepository employeeLetterDetailsRepository;

	@Override
	public String generateLatter(LetterDTO latterDTO) {
		String html = "<!DOCTYPE math \r\n" + "    PUBLIC \"-//W3C//DTD MathML 3.0//EN\"\r\n"
				+ "           \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">" + "<html>\r\n" + "    <head>\r\n"
				+ "        <style>\r\n" + "            #header {\r\n" + "                position: running(header);\r\n"
				+ "                text-align: left;\r\n" + "                margin-top: 50pt;\r\n"
				+ "                margin-left: 320pt;\r\n" + "                font-family: Garamond;\r\n"
				+ "            }\r\n" + "            \r\n" + "            @page {\r\n"
				+ "                margin-top: 25pt;\r\n" + "                margin-right: 30pt;\r\n"
				+ "                margin-bottom: 25pt;\r\n" + "                margin-left: 30pt;\r\n"
				+ "                \r\n" + "                @top-right {\r\n"
				+ "                    content: element(header);\r\n" + "                }\r\n" + "               \r\n"
				+ "                @bottom-center {\r\n"
				+ "                    content: \"Page \" counter(page) \" of \" counter(pages);\r\n"
				+ "                }\r\n" + "\r\n" + "            }\r\n" + "            img{\r\n"
				+ "                height:100% ;\r\n" + "                width: 100%;\r\n" + "            }\r\n"
				+ "        </style>\r\n" + "    </head>" + "<body>\r\n" + latterDTO.getContent() + "</body>\r\n"
				+ "</html>\r\n" + "";
		String xhtml = htmlToXhtml(html);
		try {
			String latterName = latterDTO.getLatterName()
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".pdf";
			xhtmlToPdf(xhtml, latterName, latterDTO);
			return "Latter Created Successfully With Name " + latterName;
		} catch (IOException | DocumentException e) {
			throw new DataNotFoundException(e.getMessage());
		}

	}

	private String htmlToXhtml(String html) {
		org.jsoup.nodes.Document document = Jsoup.parse(html);
		document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
		return document.html();
	}

	private void xhtmlToPdf(String xhtml, String outFileName, LetterDTO latterDTO)
			throws DocumentException, IOException {
		File output = new File(outFileName);
		ITextRenderer iTextRenderer = new ITextRenderer();
		iTextRenderer.setDocumentFromString(xhtml);
		iTextRenderer.layout();
		OutputStream os = new FileOutputStream(output);
		iTextRenderer.createPDF(os);
		MultipartFile convert = fileToMultipartConverter.convert(output);
		String uploadFile = s3UploadFile.uploadFile(convert);
		EmployeeDocumentDetails documentDetails = Optional.of(documentDetailsRepository
				.findByEmployeeIdAndCompanyId(latterDTO.getEmployeeId(), latterDTO.getCompanyId()))
				.filter(x -> !x.isEmpty()).map(docs -> {
					EmployeeDocumentDetails employeeDocumentDetails = docs.get(0);
					employeeDocumentDetails.getDocuments().put(latterDTO.getLatterName(), uploadFile);
					return employeeDocumentDetails;
				})
				.orElseGet(() -> EmployeeDocumentDetails.builder().companyId(latterDTO.getCompanyId())
						.documentId(generator.generateSequence("latter_sequence_name"))
						.employeeId(latterDTO.getEmployeeId()).documents(Map.of(latterDTO.getLatterName(), uploadFile))
						.build());
		documentDetailsRepository.save(documentDetails);
		if (output.exists()) {
			Path path = Paths.get(outFileName);
			Files.delete(path);
		}
		os.close();
	}

	@Override
	public List<EmployeeBasicDetailsDTO> getEmployeeDetails(Long companyId) {
		return employeePersonalInfoRepository.getBasicDetails(companyId);
	}

	@Override
	@Transactional
	public LetterDetailsDTO saveLetter(LetterDetailsDTO letterDetailsDTO, MultipartFile file, Long companyId,
			Long userId) {
		if (file.isEmpty()) {
			throw new DataNotFoundException("File Not Found");
		}
		EmployeePersonalInfo employee = employeePersonalInfoRepository.findById(letterDetailsDTO.getEmployeeInfoId())
				.orElseThrow(() -> new DataNotFoundException("Employee Not Found"));
		List<EmployeeLetterDetails> employeeLetter = employeeLetterDetailsRepository
				.findByEmployeeInfoIdAndCompanyId(letterDetailsDTO.getEmployeeInfoId(), companyId);
		EmployeeLetterDetails employeeLetterDetails = employeeLetter.isEmpty()
				? EmployeeLetterDetails.builder().companyId(companyId).employeeInfoId(employee.getEmployeeInfoId())
						.letters(new ArrayList<>()).build()
				: employeeLetter.get(employeeLetter.size() - 1);
		employeeLetterDetails.getLetters()
				.add(LetterDetails.builder().id(Long.valueOf(employeeLetterDetails.getLetters().size() + 1))
						.issuedDate(LocalDate.now()).issuedBy(userId).type(letterDetailsDTO.getType())
						.url(s3UploadFile.uploadFile(file)).build());
		employeeLetterDetailsRepository.save(employeeLetterDetails);
		return letterDetailsDTO;
	}

	@Override
	public List<LetterDetailsDTO> getEmployeeLetters(Long employeeInfoId, Long companyId) {
		List<LetterDetailsDTO> letterDetailsDTOList = new ArrayList<>();
		List<EmployeeLetterDetails> employeeLetter = employeeLetterDetailsRepository
				.findByEmployeeInfoIdAndCompanyId(employeeInfoId, companyId);
		if (!employeeLetter.isEmpty()) {
			EmployeeLetterDetails employeeLetterDetails = employeeLetter.get(0);
			List<LetterDetails> letters = employeeLetterDetails.getLetters();
			if (letters != null) {
				letters.stream().filter(letter-> letter.getIsApproved()!= null && letter.getIsApproved())
						.forEach(letter -> letterDetailsDTOList.add(LetterDetailsDTO.builder().type(letter.getType())
								.url(letter.getUrl()).issuedDate(letter.getIssuedDate()).build()));
			}
		}
		return letterDetailsDTOList;
	}

}
