import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Charity} from '../response/charity';

@Injectable({
  providedIn: 'root'
})
export class DonationService {

  constructor(private httpClient: HttpClient) { }

  getCharityDetails(id: number): Observable<Charity> {
    return this.httpClient.get<Charity>('https://api.staging.justgiving.com/redacted/v1/charity/' + id);
  }
}
