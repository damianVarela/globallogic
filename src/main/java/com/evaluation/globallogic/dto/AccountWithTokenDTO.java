package com.evaluation.globallogic.dto;

import com.evaluation.globallogic.entity.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountWithTokenDTO extends Account {

    private String token;
}
