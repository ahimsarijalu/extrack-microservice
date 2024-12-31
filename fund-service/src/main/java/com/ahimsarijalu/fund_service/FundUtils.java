package com.ahimsarijalu.fund_service;

import com.ahimsarijalu.fund_service.dtos.ExpenseDTO;
import com.ahimsarijalu.fund_service.dtos.FundDTO;
import com.ahimsarijalu.fund_service.model.Fund;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class FundUtils {
    public static FundDTO mapFundToDTO(Fund fund, List<ExpenseDTO> expenses) {
        FundDTO fundDTO = new FundDTO();
        BeanUtils.copyProperties(fund, fundDTO);
        fundDTO.setId(fund.getId().toString());
        fundDTO.setUserId(fund.getUserId().toString());
        fundDTO.setExpenses(expenses);
        return fundDTO;
    }

    public static FundDTO mapFundToDTOWithoutExpenses(Fund fund) {
        FundDTO fundDTO = new FundDTO();
        BeanUtils.copyProperties(fund, fundDTO);
        fundDTO.setId(fund.getId().toString());
        fundDTO.setUserId(fund.getUserId().toString());
//        fundDTO.setExpenses(fund.getExpenses().stream().map(ExpenseUtil::mapExpenseToDTO).toList());
        return fundDTO;
    }
}
