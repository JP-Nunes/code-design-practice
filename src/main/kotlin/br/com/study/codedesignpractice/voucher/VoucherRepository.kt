package br.com.study.codedesignpractice.voucher

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VoucherRepository: JpaRepository<Voucher, Long>