import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import * as L from 'leaflet';

@Component({
  selector: 'app-leaflet',
  templateUrl: './leaflet.component.html',
  styleUrls: ['./leaflet.component.css']
})
export class LeafletComponent implements OnInit, AfterViewInit {
  private map: any;
  public checkoutForm = this.formBuilder.group({
    content: '',
  });
  constructor(
    private formBuilder: FormBuilder,
  ) { }

  private initMap(): void {
    this.map = L.map('map', {
      center: [39.8282, -98.5795],
      zoom: 3
    }).setView([51.505, -0.09], 13);

    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      minZoom: 3,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });

    tiles.addTo(this.map);


    L.marker([51.5, -0.09]).addTo(this.map)
      .bindPopup('A pretty CSS3 popup.<br> Easily customizable.')
      .openPopup();
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnInit(): void {
  }
  onSubmit(): void {
    const value = this.checkoutForm.get('content')?.value;
    console.warn('Your order has been submitted', value);
    L.marker([50.5, 30.5]).addTo(this.map)
      .bindPopup(value)
      .openPopup();

    // this.checkoutForm.reset();
  }
}
