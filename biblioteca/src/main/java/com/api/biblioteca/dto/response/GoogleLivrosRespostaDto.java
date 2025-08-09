package com.api.biblioteca.dto.response;

import java.util.List;

public record GoogleLivrosRespostaDto(List<Item> items) {
    public record Item(VolumeInfo volumeInfo) {}
    public record VolumeInfo(
        String title,
        List<String> authors,
        List<String> categories,
        String publishedDate,
        List<IndustryIdentifier> industryIdentifiers
    ) {}
    public record IndustryIdentifier(String type, String identifier) {}
}
