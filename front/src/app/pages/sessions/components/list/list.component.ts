import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { RouterModule } from '@angular/router';

import { MaterialModule } from '../../../../shared/material.module';
import { Session } from '../../../../core/models/session.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionInformation } from '../../../../core/models/sessionInformation.interface';
import { SessionService } from '../../../../core/service/session.service';

@Component({
  selector: 'app-list',
  imports: [CommonModule, MaterialModule, RouterModule],
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
  standalone: true,
})
export class ListComponent {
  private sessionApiService = inject(SessionApiService);
  private sessionService = inject(SessionService);

  public sessions$: Observable<Session[]> = this.sessionApiService.all();

  public get user(): SessionInformation | undefined {
    return this.sessionService.sessionInformation;
  }
}
