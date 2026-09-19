package com.hotel.lostfound.inventory.web;

import com.hotel.lostfound.inventory.ItemCategory;
import com.hotel.lostfound.inventory.MapPoint;
import com.hotel.lostfound.inventory.application.InventoryService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/items")
class ItemController {

    private final InventoryService inventory;

    ItemController(InventoryService inventory) {
        this.inventory = inventory;
    }

    @GetMapping
    List<FoundItemResponse> list() {
        return inventory.list().stream().map(FoundItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    FoundItemResponse get(@PathVariable UUID id) {
        return FoundItemResponse.from(inventory.get(id));
    }

    @GetMapping("/{id}/photo")
    ResponseEntity<byte[]> photo(@PathVariable UUID id) {
        var stored = inventory.photo(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                .contentType(MediaType.parseMediaType(stored.contentType()))
                .body(stored.bytes());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('HOUSEKEEPING')")
    FoundItemResponse log(
            @RequestParam String description,
            @RequestParam ItemCategory category,
            @RequestParam String zoneName,
            @RequestParam(required = false) Double mapX,
            @RequestParam(required = false) Double mapY,
            @RequestParam(required = false) MultipartFile photo,
            @AuthenticationPrincipal Jwt jwt) {
        MapPoint point = mapX == null || mapY == null ? null : new MapPoint(mapX, mapY);
        return FoundItemResponse.from(inventory.logItem(description, category, zoneName, point, username(jwt), photo));
    }

    @PostMapping("/{id}/store")
    @PreAuthorize("hasAnyRole('HOUSEKEEPING','DUTY_MANAGER')")
    FoundItemResponse store(@PathVariable UUID id, @RequestParam String location) {
        return FoundItemResponse.from(inventory.moveToStorage(id, location));
    }

    @PostMapping("/{id}/claim")
    @PreAuthorize("hasAnyRole('FRONT_DESK','DUTY_MANAGER')")
    FoundItemResponse claim(@PathVariable UUID id) {
        return FoundItemResponse.from(inventory.openClaim(id));
    }

    @PostMapping("/{id}/reclaim")
    @PreAuthorize("hasRole('DUTY_MANAGER')")
    FoundItemResponse reclaim(@PathVariable UUID id) {
        return FoundItemResponse.from(inventory.reclaim(id));
    }

    @PostMapping("/{id}/unclaimed")
    @PreAuthorize("hasRole('DUTY_MANAGER')")
    FoundItemResponse unclaimed(@PathVariable UUID id) {
        return FoundItemResponse.from(inventory.markUnclaimed(id));
    }

    @PostMapping("/{id}/dispose")
    @PreAuthorize("hasRole('DUTY_MANAGER')")
    FoundItemResponse dispose(@PathVariable UUID id) {
        return FoundItemResponse.from(inventory.dispose(id));
    }

    private static String username(Jwt jwt) {
        String preferred = jwt.getClaimAsString("preferred_username");
        return preferred == null || preferred.isBlank() ? jwt.getSubject() : preferred;
    }
}
