import {AfterContentChecked, Component, OnDestroy, OnInit} from '@angular/core';
import {Player} from '../response/player';
import {AppService} from '../app.service';
import {Donation} from '../response/donation';
import {DeathService} from './death.service';

@Component({
  selector: 'app-deaths',
  templateUrl: './deaths.component.html',
  styleUrls: ['./deaths.component.css']
})
export class DeathsComponent implements OnInit, OnDestroy {

  deaths: Player[];
  playerPing;

  constructor(private deathService: DeathService) {}

  ngOnInit(): void {
    this.getPlayerStats();
    this.playerPing = setInterval(() => {
      this.getPlayerStats();
    },  10000);
  }

  ngOnDestroy(): void {
    clearInterval(this.playerPing);
  }

  getPlayerStats(): void {
    this.deathService.getPlayerStats().subscribe(response => {
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
