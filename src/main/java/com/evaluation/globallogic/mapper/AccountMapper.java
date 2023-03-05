package com.evaluation.globallogic.mapper;

import com.evaluation.globallogic.dto.AccountWithTokenDTO;
import com.evaluation.globallogic.dto.PhoneDTO;
import com.evaluation.globallogic.entity.Account;
import com.evaluation.globallogic.entity.Phone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(target = "token", source = "token")
    AccountWithTokenDTO accountWithTokenDTO(Account account, String token);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source = "account")
    Phone toPhoneEntity(PhoneDTO dto, Account account);
}
