import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

// Provides HTTP client used to make HTTP requests within the Angular application
// Returns Observables (can be synchronous), not Promises (always asynchronous)
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

const API_URL = environment.apiUrl;

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  }

  constructor(private http: HttpClient) { }
  

  /**
   * Creates new check-in for specific store ID
   * @param storeId ID of the store that the check-in occurs for
   * @returns Observable 
   */
  public createCheckIn(storeId: string, busy: number, line: number, hygiene: number, mask: number): Observable<Object> {
    let params = new HttpParams();

    // FIXME: Must convert to double in the backend
    params = params
              .set('storeId', storeId.toString())
              .set('busy', busy.toString())
              .set('line', line.toString())
              .set('hygiene', hygiene.toString())
              .set('mask', mask.toString());
    return this.http
      .post(API_URL + '/checkin', params, this.httpOptions)
      .pipe(
        tap(_ => console.log("added new checkin")),
        catchError(error => throwError(error.message || error))
      );
  }
}