package br.com.alesaudate.contas.domain.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryNotFoundException extends BusinessException{

    String name;
}
