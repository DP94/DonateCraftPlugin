import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PlayerResponse} from '../response/player.response';
import {environment} from '../../environments/environment';
import {RevivalLocksResponse} from '../response/revival.locks.response';

@Injectable({
  providedIn: 'root'
})
export class DeathService {
  constructor(private http: HttpClient) {
  }

  getPlayerStats(): Observable<PlayerResponse> {
    return this.http.get<PlayerResponse>(environment.apiUrl + 'players');
  }

  getPlayerLocks(): Observable<RevivalLocksResponse> {
    return this.http.get<RevivalLocksResponse>(environment.apiUrl + 'lock');
  }
}
