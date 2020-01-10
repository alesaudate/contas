package br.com.alesaudate.contas.domain.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CategoryNotFoundException extends BusinessException{

    String name;
}
