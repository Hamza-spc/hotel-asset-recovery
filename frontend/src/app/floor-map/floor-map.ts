import {
  AfterViewInit,
  Component,
  ElementRef,
  NgZone,
  OnDestroy,
  effect,
  inject,
  input,
  output,
  viewChild,
} from '@angular/core';
import { Router } from '@angular/router';
import L, { type Layer } from 'leaflet';
import type { Feature, GeoJsonObject } from 'geojson';
import { OperationsApi, ZoneResolved } from '../core/operations-api';

export interface FloorPin {
  x: number;
  y: number;
  label: string;
  kind: 'item' | 'report' | 'pick';
  href?: string;
}

export interface MapPick {
  x: number;
  y: number;
  zoneName: string;
}

const ZONE_STYLE: Record<string, L.PathOptions> = {
  Storage: { color: '#c4a574', fillColor: '#8b6b3d', fillOpacity: 0.18, weight: 1.5 },
  'Guest wing': { color: '#5b5348', fillColor: '#3a342c', fillOpacity: 0.08, weight: 1 },
  Lobby: { color: '#c4a574', fillColor: '#4a3f2e', fillOpacity: 0.12, weight: 1 },
  Restaurant: { color: '#c4a574', fillColor: '#3d3328', fillOpacity: 0.12, weight: 1 },
  Gym: { color: '#7d9a86', fillColor: '#2e3a32', fillOpacity: 0.16, weight: 1 },
  Pool: { color: '#7d93a3', fillColor: '#2a3540', fillOpacity: 0.16, weight: 1 },
};

@Component({
  selector: 'app-floor-map',
  templateUrl: './floor-map.html',
  styleUrl: './floor-map.scss',
  host: {
    '[class.tall]': 'tall()',
  },
})
export class FloorMap implements AfterViewInit, OnDestroy {
  private readonly host = viewChild.required<ElementRef<HTMLDivElement>>('host');
  private readonly api = inject(OperationsApi);
  private readonly router = inject(Router);
  private readonly zone = inject(NgZone);

  readonly pins = input<FloorPin[]>([]);
  readonly selectable = input(false);
  readonly tall = input(false);
  readonly pinPicked = output<MapPick>();

  private map?: L.Map;
  private pinLayer?: L.LayerGroup;

  constructor() {
    effect(() => {
      const current = this.pins();
      this.drawPins(current);
    });
  }

  ngAfterViewInit() {
    const bounds = L.latLngBounds([0, 0], [70, 100]);
    this.map = L.map(this.host().nativeElement, {
      crs: L.CRS.Simple,
      minZoom: 1,
      maxZoom: 5,
      zoomSnap: 0.25,
      attributionControl: false,
      zoomControl: true,
    });
    this.map.fitBounds(bounds);
    this.map.setMaxBounds(bounds.pad(0.08));
    L.imageOverlay('/hotel-ground.svg', bounds, { opacity: 0.95, interactive: false }).addTo(this.map);
    this.api.zones().subscribe((geojson) => {
      if (!this.map) {
        return;
      }
      L.geoJSON(geojson as GeoJsonObject, {
        style: (feature) => {
          const name = feature?.properties?.['name'];
          return ZONE_STYLE[typeof name === 'string' ? name : ''] ?? { color: '#c4a574', weight: 1 };
        },
        onEachFeature: (feature: Feature, layer: Layer) => {
          const name = feature.properties?.['name'];
          if (typeof name === 'string') {
            layer.bindTooltip(name, { sticky: true, className: 'zone-tip' });
          }
          layer.on('click', (event: L.LeafletMouseEvent) => this.onMapClick(event));
        },
      }).addTo(this.map);
    });
    this.pinLayer = L.layerGroup().addTo(this.map);
    this.drawPins(this.pins());
    this.map.on('click', (event: L.LeafletMouseEvent) => this.onMapClick(event));
    window.setTimeout(() => this.map?.invalidateSize(), 80);
  }

  ngOnDestroy() {
    this.map?.remove();
  }

  private onMapClick(event: L.LeafletMouseEvent) {
    if (!this.selectable()) {
      return;
    }
    const x = round(clamp(event.latlng.lng, 0, 100));
    const y = round(clamp(event.latlng.lat, 0, 70));
    this.zone.run(() => {
      this.api.resolveZone(x, y).subscribe({
        next: (zone: ZoneResolved) => this.pinPicked.emit({ x, y, zoneName: zone.name }),
        error: () => this.pinPicked.emit({ x, y, zoneName: 'Unknown' }),
      });
    });
  }

  private drawPins(pins: FloorPin[]) {
    if (!this.pinLayer) {
      return;
    }
    this.pinLayer.clearLayers();
    for (const pin of pins) {
      const marker = L.circleMarker([pin.y, pin.x], {
        radius: pin.kind === 'pick' ? 8 : 7,
        color: pin.kind === 'report' ? '#e0b56a' : '#c4a574',
        fillColor: pin.kind === 'report' ? '#100f0d' : '#c4a574',
        fillOpacity: pin.kind === 'report' ? 0.15 : 0.95,
        weight: 2,
      });
      marker.bindTooltip(pin.label, { direction: 'top', className: 'zone-tip' });
      if (pin.href) {
        marker.on('click', () => this.router.navigateByUrl(pin.href!));
      }
      marker.addTo(this.pinLayer);
    }
  }
}

function clamp(value: number, min: number, max: number) {
  return Math.min(max, Math.max(min, value));
}

function round(value: number) {
  return Math.round(value * 10) / 10;
}
