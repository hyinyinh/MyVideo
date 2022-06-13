package com.hy.tiktok.exceptions;

import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.grace.ResponseStatusEnum;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一异常拦截处理
 * 可以针对异常的类型进行捕获，然后返回json信息到前端
 */
@ControllerAdvice
public class GraceExceptionHandler {

    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyException(MyCustomException e) {
        //e.printStackTrace();
        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GraceJSONResult returnMethodArgumentNotValid(MethodArgumentNotValidException e){
            BindingResult result = e.getBindingResult();
            //BindingResult保存了错误的验证信息返回前端
            HashMap<String, String> errors = getErrors(result);
            return GraceJSONResult.errorMap(errors);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public GraceJSONResult returnMaxUploadSize(MaxUploadSizeExceededException e){
        e.printStackTrace();
        return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_MAX_SIZE_2MB_ERROR);
    }

    public HashMap<String, String> getErrors(BindingResult result){
        HashMap<String,String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            //错误对应的字段名
            String field = error.getField();
            //错误的信息
            String msg = error.getDefaultMessage();
            map.put(field,msg);
        }
        return map;
    }
}
