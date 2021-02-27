import {AfterContentChecked, Component, OnInit} from '@angular/core';
import {Player} from '../response/player';
import {AppService} from '../app.service';
import {Donation} from '../response/donation';

@Component({
  selector: 'app-deaths',
  templateUrl: './deaths.component.html',
  styleUrls: ['./deaths.component.css']
})
export class DeathsComponent implements OnInit {

  deaths: Player[];

  constructor(private appService: AppService) {
  }

  ngOnInit(): void {
    this.appService.getPlayerStats().subscribe(response => {
      this.deaths = response.players;
    });
  }

  getTotalDonationsForPlayer(donations: Donation[]): number {
    let total = 0;
    for (const donation of donations) {
      total += donation.amount;
    }
    return total;
  }
}
