/**
 * @description form 전송시 유효성 검사를 진행하는 스트립트 [JQuery Validation 플러그인 적용]
 * @filename Validation.js
 * @version 1.0
 *
 * 수정일            수정자		 Function 명
 * ------------    ---------    ----------------------------
 *
 **/

class Validation{

    /* 기능에 따른 유효성 검사 규칙 정의 */
    loginRule = {
        userId:{
            required: true
        },
        userPwd:{
            required: true
        }
    }

    joinRule = {
        userName:{
            required: true,
            minlength: 2,
            maxlength: 10
        },
        userId:{
            required: true,
            minlength: 4,
            maxlength: 20,
        },
        userPwd:{
            required: true,
            minlength: 4,
            maxlength: 20,
            regex: /^(?=.*[a-zA-Z])(?=.*[!@#$%^*-])(?=.*[0-9]).{4,20}$/
        }
    }

    constructor() {
        this.validationSet();
        this.validationMsg();
    }

    /**
     * @description form의 유효성 검사를 진행시, 체크할 규칙 정의
     */
    validationSet(){
        // 정규식 체크
        $.validator.addMethod("regex", function(value, element, regexpr) {
            return regexpr.test(value);
        });

        // 전화번호 정규식 체크
        $.validator.addMethod("tel", function(value, element) {
            value = value.replace(/\s+/g, "");
            return this.optional(element) || value.length > 9 && value.match(/^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-?[0-9]{3,4}-?[0-9]{4}$/);
        }, "전화번호 양식에 맞게 입력하세요.");

        // 이메일 정규식 체크
        $.validator.addMethod("email", function(value, element) {
            value = value.replace(/\s+/g, "");
            return this.optional(element) || value.length > 9 && value.match(/^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/);
        }, "이메일 양식에 맞게 입력하세요.");

    }

    /**
     * @description form의 유효성 검사를 진행하여 유효하지 않으면 표출할 메세지를 정의
     */
    validationMsg(){
        // 검증식 에러 메시지
        $.validator.setDefaults({
            messages: {
                userName:{
                    required: "사용자명을 입력하세요.",
                    minlength: "사용자명은 2~10자 한글 이어야 합니다.",
                    maxlength: "사용자명은 2~10자 한글 이어야 합니다.",
                },
                userId:{
                    required: "사용자 ID를 입력하세요.",
                    minlength: "사용자 ID는 4~20자 영문자 또는 숫자이어야 합니다.",
                    maxlength: "사용자 ID는 4~20자 영문자 또는 숫자이어야 합니다.",
                    regex: "사용자 ID는 4~20자 영문자 또는 숫자이어야 합니다."
                },
                userPwd:{
                    required: "비밀번호를 입력하세요.",
                    minlength: "비밀번호는 4~20자 영문자,숫자,특수문자 조합 이어야 합니다.",
                    maxlength: "비밀번호는 4~20자 영문자,숫자,특수문자 조합 이어야 합니다.",
                    regex: "비밀번호는 4~20자 영문자,숫자,특수문자 조합 이어야 합니다."
                },
                chkPwd:{
                    required: "비밀번호 확인을 입력하세요.",
                    equalTo: "비밀번호가 일치하지 않습니다."
                },
                adminPw:{
                    required: "관리자 비밀번호를 입력하세요."
                },
                chkUdtPwd:{
                    required: "비밀번호 확인을 입력하세요.",
                    equalTo: "비밀번호가 일치하지 않습니다."
                },
                lyrNmEn:{
                    required: "레이어 영문명을 입력하세요.",
                    regex: "레이어 영문명은 영문자, 숫자만 입력 가능합니다."
                },
            }
        });
    }

    /**
     * @description form의 유효성 검사를 진행, 유효하면 true, 유효하지 않으면 false를 리턴하고 유효하지 않은 속성의 메세지를 alert.
     * @param {String} formId 유효성 검사를 진행할 form id
     * @param {Object} rules 유효성 검사 규칙
     */
    form(formId, rules){
        $("#"+formId).validate({
            rules: rules,
            errorPlacement: function(error, element) {
                return true;
            }
        });

        if ($("#"+formId).valid()){     // 유효성 검사 통과
            return true;
        }else{                          // 유효성 검사 불통
            const errorList = $("#"+formId).validate().errorList;
            const errorMessage = errorList[0].message;
            CustomAlert.error(errorMessage);

            return false;
        }
    }
}