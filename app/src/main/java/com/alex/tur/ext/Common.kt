package com.alex.tur.ext

inline fun <A, B, R> ifNotNull(a: A?, b: B?, block: (A, B) -> R): R? {
    if (a != null && b != null) {
        return block(a, b)
    }
    return null
}

inline fun <A, B, C, R> ifNotNull(a: A?, b: B?, c: C?,  block: (A, B, C) -> R): R? {
    if (a != null && b != null && c != null) {
        return block(a, b, c)
    }
    return null
}