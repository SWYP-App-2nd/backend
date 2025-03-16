package kr.swyp.backend.common.desciptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.springframework.restdocs.payload.FieldDescriptor;

public class ErrorDescriptor {

    public static final FieldDescriptor[] errorResponseFieldDescriptors = {
            fieldWithPath("code").description("오류 코드"),
            fieldWithPath("message").description("오류 메시지")
    };

    private ErrorDescriptor() {
    }
}

