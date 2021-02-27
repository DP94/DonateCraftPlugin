import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Charity} from '../response/charity';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DonationService {

  constructor(private httpClient: HttpClient) { }

  getCharityDetails(id: number): Observable<Charity> {
    return this.httpClient.get<Charity>(environment.justGivingAPIUrl + '/charity/' + id);
  }
}
