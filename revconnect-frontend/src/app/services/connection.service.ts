import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Connection } from '../models/connection.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ConnectionService {
  private apiUrl = `${environment.apiUrl}/connections`;

  constructor(private http: HttpClient) {}

  sendConnectionRequest(senderId: number, receiverId: number): Observable<Connection> {
    return this.http.post<Connection>(`${this.apiUrl}/request`, { receiverId });
  }

  acceptConnection(id: number): Observable<Connection> {
    return this.http.put<Connection>(`${this.apiUrl}/${id}/accept`, {});
  }

  rejectConnection(id: number): Observable<Connection> {
    return this.http.put<Connection>(`${this.apiUrl}/${id}/reject`, {});
  }

  getMyConnections(userId: number): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.apiUrl}/user/${userId}`);
  }

  // GET /api/connections/pending/received — requests sent TO current user
  getPendingReceived(): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.apiUrl}/pending/received`);
  }

  getPendingReceivedCount(): Observable<number> {
    return this.getPendingReceived().pipe(
      map((conns: Connection[]) => conns.length)
    );
  }

  // GET /api/connections/pending/sent — requests sent BY current user
  getPendingSent(): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.apiUrl}/pending/sent`);
  }

  removeConnection(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
