import { Component, OnInit } from '@angular/core';
import {Charity} from '../response/charity';
import {DonationService} from './donation.service';
import {ActivatedRoute} from '@angular/router';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-donation',
  templateUrl: './donation.component.html',
  styleUrls: ['./donation.component.css']
})
export class DonationComponent implements OnInit {

  charities: Charity[];
  finishedLoading = false;
  charitiesLoaded = 0;
  playerKey = '';
  directiveEnv = environment;

  charityIds = new Array<number>();
  constructor(private donationService: DonationService, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {

    this.charities = new Array<Charity>();
    // Can retrieve these from DB at a later time
    this.charityIds.push(13441, 2357, 255811, 2201, 182244, 233, 11200, 300);
    this.charityIds.forEach((id) => {
      this.donationService.getCharityDetails(id).subscribe(response => {
        this.charities.push(response);
        this.charitiesLoaded++;
        this.finishedLoading = (this.charitiesLoaded === this.charityIds.length);
        if (this.finishedLoading) {
          this.activatedRoute.queryParams.subscribe(params => {
            this.playerKey = params.key;
          });
        }
      });
    });
  }

}
