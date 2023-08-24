package com.te.flinko.service.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.account.CreatWorkOrderDealDropdownDto;
import com.te.flinko.dto.account.WorkOrderDTO;
import com.te.flinko.dto.account.WorkOrderListDto;
import com.te.flinko.dto.account.WorkOrderResourcesDTO;
import com.te.flinko.entity.account.CompanyWorkOrder;
import com.te.flinko.entity.account.WorkOrderResources;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.sales.CompanyClientInfo;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.account.CompanyWorkOrderRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkOrderServiceImpl implements WorkOrderService {

	@Autowired
	CompanyWorkOrderRepository companyOrderRepository;

	@Autowired
	EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Autowired
	CompanyInfoRepository companyInfoRepo;

	@Override
	public List<WorkOrderListDto> getWorkOrderList(Long companyId) {

		List<CompanyWorkOrder> companyWorkOrderList = companyOrderRepository.findByCompanyInfoCompanyId(companyId);
		List<WorkOrderListDto> companyWorkOrderDtoList = new ArrayList<>();

		companyWorkOrderList.stream().forEach(order -> {
			WorkOrderListDto orderListDto = new WorkOrderListDto();
			orderListDto.setCost(order.getEstimatedCost());
			orderListDto.setWorkTitle(order.getWorkTitle());
			orderListDto.setDeals(order.getCompanyClientInfo().getClientName());
			orderListDto.setDepartmentName(order.getDepartmentName());
			orderListDto.setNoOfEmployee(order.getNoOfEmployee());
			orderListDto.setPriority(order.getPriority());
			orderListDto.setRequestedTo(order.getEmployeePersonalInfo() == null ? null
					: order.getEmployeePersonalInfo().getFirstName() + " "
							+ order.getEmployeePersonalInfo().getLastName());
			orderListDto.setStatus(order.getStatus());
			orderListDto.setWorkOrderId(order.getWorkOrderId());

			Optional<EmployeePersonalInfo> employeePersonalInfos = employeePersonalInfoRepository
					.findById(order.getCreatedBy());
			if (employeePersonalInfos.get() != null) {
				EmployeePersonalInfo employeePersonalInfo = employeePersonalInfos.get();
				orderListDto.setWorkOrderOwner(
						employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
			}
			companyWorkOrderDtoList.add(orderListDto);
		});
		Collections.reverse(companyWorkOrderDtoList);
		return companyWorkOrderDtoList;
	}

	@Override
	public List<CreatWorkOrderDealDropdownDto> getCompanyDeals(Long companyId) {
		Optional<CompanyInfo> companyInfos = companyInfoRepo.findById(companyId);
		List<CreatWorkOrderDealDropdownDto> dealsDto = new ArrayList<>();
		if (companyInfos.isPresent()) {
			CompanyInfo companyInfo = companyInfos.get();

			List<CompanyClientInfo> companyClientInfoList = companyInfo.getCompanyClientInfoList();
			companyClientInfoList.stream().forEach(client -> {
				CreatWorkOrderDealDropdownDto deal = new CreatWorkOrderDealDropdownDto();
				deal.setClientId(client.getClientId());
				deal.setDeal(client.getClientName());

				dealsDto.add(deal);

			});
		}
		return dealsDto;
	}

	@Override
	public WorkOrderDTO getWorkOrderDetails(Long companyId, Long workOrderId, Long employeeInfoId) {
		log.info("Get the details of work order against companyId and workOrderId ::", companyId, workOrderId);

		CompanyWorkOrder companyWorkOrder = companyOrderRepository
				.findByCompanyInfoCompanyIdAndWorkOrderId(companyId, workOrderId)
				.orElseThrow(() -> new DataNotFoundException("Work order data not found"));
		EmployeePersonalInfo employee = employeePersonalInfoRepository.findById(companyWorkOrder.getCreatedBy())
				.orElseThrow(() -> new DataNotFoundException("Owner Not Found"));
		WorkOrderDTO workOrderListDto = new WorkOrderDTO();
		BeanUtils.copyProperties(companyWorkOrder, workOrderListDto);
		workOrderListDto.setWorkOrderOwner(employee.getFirstName() + " " + employee.getLastName());
		List<WorkOrderResourcesDTO> resourcesDTO = companyWorkOrder.getWorkOrderResourcesList().stream().map(x -> {
			WorkOrderResourcesDTO workOrderResourcesDTO = new WorkOrderResourcesDTO();
			BeanUtils.copyProperties(x, workOrderResourcesDTO);
			return workOrderResourcesDTO;
		}).collect(Collectors.toList());
		workOrderListDto.setWorkOrderResources(resourcesDTO);
		workOrderListDto.setEstimatedCost(companyWorkOrder.getEstimatedCost());
		workOrderListDto.setWorkTitle(companyWorkOrder.getWorkTitle());
		workOrderListDto.setDeals(companyWorkOrder.getCompanyClientInfo().getClientName());
		workOrderListDto.setDepartmentName(companyWorkOrder.getDepartmentName());
		workOrderListDto.setNoOfEmployee(companyWorkOrder.getNoOfEmployee());
		workOrderListDto.setPriority(companyWorkOrder.getPriority());
		workOrderListDto.setStatus(companyWorkOrder.getStatus());
		workOrderListDto.setIsCostEstimated(companyWorkOrder.getEstimatedCost() != null);
		return workOrderListDto;
	}
}
