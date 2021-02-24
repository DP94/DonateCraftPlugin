import {Component, OnInit} from '@angular/core';
import {AppService} from './app.service';
import {Death} from './response/death';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'DonateCraftNG';

  deaths: Death[];

  constructor(private appService: AppService) {
  }

  ngOnInit(): void {
    this.appService.getUserDeathStats().subscribe(response => {
      this.deaths = response.deaths;
    });
  }

}
