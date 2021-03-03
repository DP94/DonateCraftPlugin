import {Component, OnInit} from '@angular/core';
import {Charity} from '../response/charity';
import {DonationService} from './donation.service';
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';

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

  charityIds = new Array<number>();

  constructor(private donationService: DonationService, private activatedRoute: ActivatedRoute) {
  }

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
          this.setPlayerKeyFromURL(this.activatedRoute);
        }
      });
    });
  }

  onJGClick(charity: Charity): void {
    window.open(environment.justGivingDonateUrl + charity.id + '?exiturl=' + environment.fullAPIUrl
                                                    + 'callback?data=JUSTGIVING-DONATION-ID|'
                                                    + this.playerKey, '_blank');
  }

  setPlayerKeyFromURL(route: ActivatedRoute): void {
    route.paramMap.subscribe(params => {
      this.playerKey = params.get('key');
    });
  }

}
