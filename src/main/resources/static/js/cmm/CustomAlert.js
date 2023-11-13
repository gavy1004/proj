/**
 * @description Alert, Confirm 등 메세지창을 SweetAlert 라이브러리 적용하기 위한 스크립트
 * @filename CustomAlert.js
 * @version 1.0
 *
 *
 **/
class CustomAlert {

    /**
     * @description 기본 alert 전역 선언
     * @type {SweetAlertOptions}
     */
    static #alert = Swal.mixin({
        width: '350px',
        title: '알림',
        heightAuto: true,
        showCloseButton: true,
        confirmButtonText: '확인',
        allowOutsideClick: false,
        allowEnterKey: false
    });

    /**
     * @description 기본 confirm 전역 선언
     * @type {SweetAlertOptions}
     */
    static #confirm = Swal.mixin({
        width: '350px',
        title: `알림`,
        heightAuto: true,
        showCloseButton: true,
        showCancelButton: true,
        confirmButtonText: '확인',
        cancelButtonText: '취소',
        allowOutsideClick: false,
        allowEnterKey: false
    });

    /**
     * @description CustomAlert 생성자 함수
     * @since 2023-06-30
     */
    constructor() {}

    /**
     * @description 관리자 인지 아닌지 확인 여부
     * @type {boolean}
     * @return {string} mng|usr 반환
     */
    static #isMng() {
        return window.location.pathname.startsWith("/mng") ? 'mng' : 'usr';
    }

    /**
     * @description alert 창 표출 (error)
     * @param {string} text 문구
     */
    static error(text) {
        const _this = this;
        return this.#alert.fire({
            html: `<p>${text}</p>`,
            icon: 'error',
        });
    }

    /**
     * @description alert 창 표출 (error 표출 후 event 있을 때)
     * @param {string} text 문구
     * @param {Function} action 이벤트
     */
    static errorAction(text, action) {
        this.error(text).then(result => {
            if (result.value) {
                action();
            }
        });
    }

    /**
     * @description alert 창 표출 (success)
     * @param {string} text 문구
     */
    static success(text) {
        const _this = this;
        return this.#alert.fire({
            html: `<p>${text}</p>`,
            icon: "success",
            //접근성 이슈로 인한 alert 내 input title 추가
            didOpen: () => {
                if ('usr' === _this.#isMng()) {
                    $('.swal2-container').find('input, select, textarea').attr('title', '알림창 입력');
                }
            }
        });
    }

    /**
     * @description alert 창 표출 (success 표출 후 event 있을 때)
     * @param {string} text 문구
     * @param {Function} action 확인 버튼 클릭 후, 이벤트
     */
    static successAction(text, action) {
        this.success(text).then(result => {
            if (result.value) {
                action();
            }
        });
    }

    /**
     * @description alert 창 표출 (warning)
     * @param {string} text 문구
     */
    static warning(text) {
        const _this = this;
        return this.#alert.fire({
            html: `<p>${text}</p>`,
            icon: 'warning',
            //접근성 이슈로 인한 alert 내 input title 추가
            didOpen: () => {
                if ('usr' === _this.#isMng()) {
                    $('.swal2-container').find('input, select, textarea').attr('title', '알림창 입력');
                }
            }
        });
    }

    /**
     * @description alert 창 표출 (warning 표출 후 event 있을 때)
     * @param {string} text 문구
     * @param {Function} action 확인 버튼 클릭 후, 이벤트
     */
    static warningAction(text, action) {
        this.warning(text).then(result => {
            if (result.value) {
                action();
            }
        });
    }

    /**
     * @description alert 창 표출 (info)
     * @param {string} text 문구
     */
    static info(text) {
        const _this = this;
        return this.#alert.fire({
            html: `<p>${text}</p>`,
            icon: "info",
            //접근성 이슈로 인한 alert 내 input title 추가
            didOpen: () => {
                if ('usr' === _this.#isMng()) {
                    $('.swal2-container').find('input, select, textarea').attr('title', '알림창 입력');
                }
            }
        });
    }

    /**
     * @description alert 창 표출 (info 표출 후 event 있을 때)
     * @param {string} text 문구
     * @param {Function} action 확인 버튼 클릭 후, 이벤트
     */
    static infoAction(text, action) {
        this.info(text).then(result => {
            if (result.value) {
                action();
            }
        });
    }

    /**
     * @description confirm 창 표출
     * @param {string} text 문구
     * @param {Function} confirmAction - 확인 버튼 클릭 후, 이벤트
     * @param {Function} [cancelAction] - [선택] 취소 버튼 클릭 후, 이벤트
     */
    static confirm(text, confirmAction, cancelAction) {
        const _this = this;
        this.#confirm.fire({
            html: `<p>${text}</p>`,
            icon: "question",
            //접근성 이슈로 인한 alert 내 input title 추가
            didOpen: () => {
                if (_this.#isMng()) {
                    $('.swal2-container').find('input, select, textarea').attr('title', '알림창 입력');
                }
            }
        }).then(result => {
            if (result.isConfirmed) { // "확인" 버튼을 클릭했을 때
                confirmAction();
            } else {
                if (cancelAction) {
                    cancelAction();
                }
            }
        });
    }
}