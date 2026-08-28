import { Component, Input, OnChanges, SimpleChanges, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Property } from '../../../core/models/property.model';

declare var L: any; // Leaflet JS reference

@Component({
  selector: 'app-property-map',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './property-map.component.html',
  styleUrls: ['./property-map.component.css']
})
export class PropertyMapComponent implements AfterViewInit, OnChanges {
  @Input() properties: Property[] = [];
  @ViewChild('mapContainer') mapContainer!: ElementRef;

  private map: any;
  private markers: any[] = [];

  ngAfterViewInit(): void {
    this.loadLeafletScript().then(() => {
      this.initMap();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['properties'] && this.map) {
      this.updateMarkers();
    }
  }

  private loadLeafletScript(): Promise<void> {
    return new Promise((resolve) => {
      if (typeof L !== 'undefined') {
        resolve();
        return;
      }

      // Append Leaflet CSS
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
      document.head.appendChild(link);

      // Append Leaflet JS
      const script = document.createElement('script');
      script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
      script.onload = () => resolve();
      document.head.appendChild(script);
    });
  }

  private initMap(): void {
    if (!this.mapContainer || this.map) return;

    // Default center (Hyderabad / India center)
    this.map = L.map(this.mapContainer.nativeElement).setView([17.4156, 78.4347], 11);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.updateMarkers();
  }

  private updateMarkers(): void {
    // Clear old markers
    this.markers.forEach(m => this.map.removeLayer(m));
    this.markers = [];

    if (!this.properties || this.properties.length === 0) return;

    const bounds: any[] = [];

    this.properties.forEach(p => {
      if (p.latitude && p.longitude) {
        const latLng = [p.latitude, p.longitude];
        bounds.push(latLng);

        const customIcon = L.divIcon({
          className: 'custom-map-pin',
          html: `<div class="pin-badge">₹ ${(p.price / 100000).toFixed(1)}L</div>`,
          iconSize: [60, 30],
          iconAnchor: [30, 15]
        });

        const marker = L.marker(latLng, { icon: customIcon }).addTo(this.map);

        const popupContent = `
          <div class="map-popup-card">
            <img src="${p.coverImageUrl}" alt="${p.title}" style="width:100%; height:110px; object-fit:cover; border-radius:8px; margin-bottom:8px;">
            <h4 style="margin:0 0 4px; font-size:14px; font-weight:700; color:#0f172a;">${p.title}</h4>
            <p style="margin:0 0 6px; font-size:12px; color:#64748b;">${p.address}, ${p.city}</p>
            <div style="font-weight:800; color:#2563eb; font-size:15px; margin-bottom:8px;">₹ ${p.price.toLocaleString()}</div>
            <a href="/properties/${p.id}" style="display:block; text-align:center; background:#2563eb; color:#fff; padding:6px; border-radius:6px; font-weight:600; font-size:12px; text-decoration:none;">View Listing →</a>
          </div>
        `;

        marker.bindPopup(popupContent);
        this.markers.push(marker);
      }
    });

    if (bounds.length > 0) {
      this.map.fitBounds(bounds, { padding: [40, 40] });
    }
  }
}
