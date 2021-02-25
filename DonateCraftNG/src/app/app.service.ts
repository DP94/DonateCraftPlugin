import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DeathsResponse} from './response/deaths.response';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  constructor(private http: HttpClient) {}

  getUserDeathStats(): Observable<DeathsResponse> {
    return this.http.get<DeathsResponse>('http://changeme/deaths');
  }
}
