import { Component, OnInit } from '@angular/core';
import {Death} from '../response/death';
import {AppService} from '../app.service';

@Component({
  selector: 'app-deaths',
  templateUrl: './deaths.component.html',
  styleUrls: ['./deaths.component.css']
})
export class DeathsComponent implements OnInit {

  deaths: Death[];

  constructor(private appService: AppService) {
  }

  ngOnInit(): void {
    this.appService.getUserDeathStats().subscribe(response => {
      this.deaths = response.deaths;
    });
  }

}
