package br.com.study.codedesignpractice.voucher

import jakarta.persistence.EntityManager

fun EntityManager.findVoucherByCode(code: String): Voucher? {
    val resultList = this.createQuery(
        "SELECT v FROM Voucher v WHERE v.code = :code",
        Voucher::class.java
    )
        .setParameter("code", code)
        .resultList

    return resultList.firstOrNull()
}
