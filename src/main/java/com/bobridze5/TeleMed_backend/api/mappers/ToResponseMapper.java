package com.bobridze5.TeleMed_backend.api.mappers;

public interface ToResponseMapper<R, E> {
    R mapToResponse(E entity);
}
