package com.bobridze5.TeleMed_backend.api.mappers;

public interface ToEntityMapper<R, E> {
    E mapToEntity(R request);
}
