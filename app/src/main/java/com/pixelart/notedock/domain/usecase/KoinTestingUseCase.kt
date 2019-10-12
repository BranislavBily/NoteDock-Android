package com.pixelart.notedock.domain.usecase

interface KoinTestingUseCase {
    fun testKoin(): String
}

class KoinTestingImpl: KoinTestingUseCase {
    override fun testKoin(): String {
        return "Koin working"
    }
}