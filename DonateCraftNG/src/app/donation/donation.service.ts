import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Charity} from '../response/charity';
import {environment} from '../../environments/environment';
import {retry, timeout} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DonationService {

  constructor(private httpClient: HttpClient) { }

  getCharityDetails(id: number): Observable<Charity> {
    return this.httpClient.get<Charity>(environment.justGivingAPIUrl + '/charity/' + id).pipe(timeout(15000), retry(2));
  }

  checkIfRevivalKeyExists(id: string): Observable<boolean> {
    return this.httpClient.get<boolean>(`${environment.apiUrl}lock/${id}`);
  }
}
