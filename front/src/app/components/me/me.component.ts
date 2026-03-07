import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { MaterialModule } from '../../shared/material.module';
import { SessionService } from '../../core/service/session.service';
import { User } from '../../core/models/user.interface';
import { UserService } from '../../core/service/user.service';

@Component({
  selector: 'app-me',
  imports: [CommonModule, MaterialModule],
  templateUrl: './me.component.html',
  styleUrls: ['./me.component.scss'],
  standalone: true,
})
export class MeComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private sessionService = inject(SessionService);
  private matSnackBar = inject(MatSnackBar);
  private userService = inject(UserService);
  private destroy$ = new Subject<void>();

  public user: User | undefined;

  public ngOnInit(): void {
    this.userService
      .getById(this.sessionService.sessionInformation!.id.toString())
      .pipe(takeUntil(this.destroy$))
      .subscribe((user: User) => (this.user = user));
  }

  public ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  public back(): void {
    window.history.back();
  }

  public delete(): void {
    this.userService
      .delete(this.sessionService.sessionInformation!.id.toString())
      .pipe(takeUntil(this.destroy$))
      .subscribe((): void => {
        this.matSnackBar.open('Your account has been deleted !', 'Close', {
          duration: 3000,
        });
        this.sessionService.logOut();
        this.router.navigate(['/']);
      });
  }
}
