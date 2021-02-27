import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PlayerResponse} from './response/player.response';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  constructor(private http: HttpClient) {}

  getPlayerStats(): Observable<PlayerResponse> {
    return this.http.get<PlayerResponse>('http://localhost:8000/players');
  }
}
