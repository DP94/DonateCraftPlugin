import { Component, OnInit } from '@angular/core';
import {Charity} from '../response/charity';
import {DonationService} from './donation.service';

@Component({
  selector: 'app-donation',
  templateUrl: './donation.component.html',
  styleUrls: ['./donation.component.css']
})
export class DonationComponent implements OnInit {

  charities: Charity[];
  finishedLoading = false;
  charitiesLoaded = 0;

  charityIds = new Array<number>();
  constructor(private donationService: DonationService) { }

  ngOnInit(): void {
    this.charities = new Array<Charity>();
    // Can retrieve these from DB at a later time
    this.charityIds.push(13441, 2357, 255811, 2201, 182244, 233);
    this.charityIds.forEach((id) => {
      this.donationService.getCharityDetails(id).subscribe(response => {
        this.charities.push(response);
        this.charitiesLoaded++;
        this.finishedLoading = (this.charitiesLoaded === this.charityIds.length);
      });
    });
  }

}
