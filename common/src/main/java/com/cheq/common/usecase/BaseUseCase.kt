package com.cheq.common.usecase

/**
 * Created by Akash Khatkale on 15th May, 2023.
 * akash.k@cheq.one
 */

interface InputUseCase<I, O> {
    suspend operator fun invoke(input: I): O
}

interface NoInputUseCase<T> {
    suspend operator fun invoke(): T
}