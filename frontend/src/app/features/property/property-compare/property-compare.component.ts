import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { PropertyService } from '../../../core/services/property.service';
import { Property } from '../../../core/models/property.model';

@Component({
  selector: 'app-property-compare',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './property-compare.component.html',
  styleUrls: ['./property-compare.component.css']
})
export class PropertyCompareComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private propertyService = inject(PropertyService);

  properties: Property[] = [];
  loading: boolean = true;

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const idsStr = params['ids'];
      if (idsStr) {
        const ids = idsStr.split(',').map((id: string) => +id);
        this.fetchProperties(ids);
      } else {
        this.loading = false;
      }
    });
  }

  fetchProperties(ids: number[]): void {
    this.loading = true;
    const reqs = ids.map(id => this.propertyService.getPropertyById(id).toPromise());
    Promise.all(reqs).then(results => {
      this.properties = results
        .filter(res => res && res.success && res.data)
        .map(res => res!.data!);
      this.loading = false;
    }).catch(err => {
      console.error('Failed to compare properties', err);
      this.loading = false;
    });
  }

  removeProperty(id: number): void {
    this.properties = this.properties.filter(p => p.id !== id);
  }

  calculatePricePerSqft(p: Property): number {
    if (!p.areaSqft || p.areaSqft === 0) return 0;
    return Math.round(p.price / p.areaSqft);
  }
}
