package com.te.flinko.service.sales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.DashboardDTO;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.sales.AllCompanyClientInfoResponseDTO;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admin.CompanyLeadCategories;
import com.te.flinko.entity.sales.CompanyClientInfo;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.sales.CompanyClientInfoRepository;

@Service
public class SalesDashboardServiceImpl implements SalesDashboardService {

	@Autowired
	private CompanyInfoRepository companyInfoRepository;

	private static final String CARDS = "Cards";

	private static final String GRAPH = "Graph";

	private static final String CONVERTED_TO_DEAL = "Converted to Deal";

	@Autowired
	private CompanyClientInfoRepository companyClientInfoRepository;

	@Override
	public DashboardResponseDTO getClientDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		CompanyInfo companyInfo = companyInfoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new DataNotFoundException("Company Not Found"));
		List<CompanyClientInfo> clientList = companyInfo.getCompanyClientInfoList();

		List<CompanyClientInfo> clientListForGraph = clientList.stream().filter(
				client -> client.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& client.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getClientCounts(clientList, CARDS))
				.graphValues(getClientCounts(clientListForGraph, GRAPH))
				.tableValues(getClientCountsForTable(clientList, companyInfo)).build();
	}

	private List<DashboardDTO> getClientCounts(List<CompanyClientInfo> clientList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long dealCount = clientList.stream()
				.filter(client -> client.getCompanyLeadCategories() != null
						&& CONVERTED_TO_DEAL.equalsIgnoreCase(client.getCompanyLeadCategories().getLeadCategoryName()))
				.count();
		Long totalCount = Long.valueOf(clientList.size());
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type("Total").count(totalCount).build());
		}
		values.add(DashboardDTO.builder().type("Lead").count(Math.subtractExact(totalCount, dealCount)).build());
		values.add(DashboardDTO.builder().type("Deal").count(dealCount).build());
		return values;
	}

	private List<DashboardDTO> getClientCountsForTable(List<CompanyClientInfo> clientList, CompanyInfo companyInfo) {
		List<CompanyLeadCategories> companyLeadCategoriesList = companyInfo.getCompanyLeadCategoriesList();
		List<DashboardDTO> values = new ArrayList<>();
		for (CompanyLeadCategories companyLeadCategories : companyLeadCategoriesList) {
			values.add(DashboardDTO.builder().type(companyLeadCategories.getLeadCategoryName())
					.count(clientList.stream()
							.filter(client -> client.getCompanyLeadCategories() != null
									&& companyLeadCategories.getLeadCategoryName()
											.equalsIgnoreCase(client.getCompanyLeadCategories().getLeadCategoryName()))
							.count())
					.build());
		}
		return values;
	}

	@Override
	public List<AllCompanyClientInfoResponseDTO> getClientDetailsByStatus(Long companyId, String type) {
		List<CompanyClientInfo> clientInfoList = companyClientInfoRepository.findByCompanyInfoCompanyId(companyId);
		switch (type.toUpperCase()) {
		case "TOTAL":
			return getClientDTO(clientInfoList);
		case "DEAL":
			return getClientDTO(clientInfoList.stream()
					.filter(client -> client.getCompanyLeadCategories() != null && CONVERTED_TO_DEAL
							.equalsIgnoreCase(client.getCompanyLeadCategories().getLeadCategoryName()))
					.collect(Collectors.toList()));
		case "LEAD":
			return getClientDTO(clientInfoList.stream().filter(client -> client.getCompanyLeadCategories() != null
					&& !(CONVERTED_TO_DEAL.equalsIgnoreCase(client.getCompanyLeadCategories().getLeadCategoryName())))
					.collect(Collectors.toList()));

		default:
			throw new DataNotFoundException("Type Not Found");
		}
	}

	private List<AllCompanyClientInfoResponseDTO> getClientDTO(List<CompanyClientInfo> clientInfoList) {
		return clientInfoList.stream().map(client -> {
			AllCompanyClientInfoResponseDTO clientDTO = new AllCompanyClientInfoResponseDTO();
			BeanUtils.copyProperties(client, clientDTO);
			CompanyLeadCategories companyLeadCategories = client.getCompanyLeadCategories();
			if (companyLeadCategories != null) {
				clientDTO.setLeadStatus(companyLeadCategories.getLeadCategoryName());
				clientDTO.setColor(companyLeadCategories.getColor());
			}
			return clientDTO;
		}).collect(Collectors.toList());

	}

}
