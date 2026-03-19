import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject } from 'rxjs/internal/Subject';
import { takeUntil } from 'rxjs/operators';

import { MaterialModule } from '../../../../shared/material.module';
import { Session } from '../../../../core/models/session.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { Teacher } from '../../../../core/models/teacher.interface';
import { TeacherService } from '../../../../core/service/teacher.service';

@Component({
  selector: 'app-detail',
  imports: [CommonModule, MaterialModule],
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss'],
})
export class DetailComponent implements OnInit {
  public session: Session | undefined;
  public teacher: Teacher | undefined;
  public isParticipate = false;
  public isAdmin = false;
  public sessionId: string;
  public userId: string;

  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private sessionService = inject(SessionService);
  private sessionApiService = inject(SessionApiService);
  private teacherService = inject(TeacherService);
  private matSnackBar = inject(MatSnackBar);
  private router = inject(Router);
  private destroy$ = new Subject<void>();
  constructor() {
    this.sessionId = this.route.snapshot.paramMap.get('id')!;
    this.isAdmin = this.sessionService.sessionInformation!.admin;
    this.userId = this.sessionService.sessionInformation!.id.toString();
  }

  public ngOnInit(): void {
    this.fetchSession();
  }

  public back(): void {
    window.history.back();
  }

  public delete(): void {
    this.sessionApiService
      .delete(this.sessionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe((): void => {
        this.matSnackBar.open('Session deleted !', 'Close', { duration: 3000 });
        this.router.navigate(['sessions']);
      });
  }

  public participate(): void {
    this.sessionApiService
      .participate(this.sessionId, this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe((): void => {
        this.fetchSession();
      });
  }

  public unParticipate(): void {
    this.sessionApiService
      .unParticipate(this.sessionId, this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe((): void => {
        this.fetchSession();
      });
  }

  private fetchSession(): void {
    this.sessionApiService
      .detail(this.sessionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe((session: Session): void => {
        this.session = session;
        this.isParticipate = session.users.some(
          (u: number): boolean =>
            u === this.sessionService.sessionInformation!.id,
        );
        this.teacherService
          .detail(session.teacher_id.toString())
          .pipe(takeUntil(this.destroy$))
          .subscribe((teacher: Teacher): void => {
            this.teacher = teacher;
          });
      });
  }

  public ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
