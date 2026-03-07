import { CommonModule } from '@angular/common';
import { Component, OnDestroy, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AuthService } from '../../core/service/auth.service';
import { MaterialModule } from '../../shared/material.module';
import { RegisterRequest } from '../../core/models/registerRequest.interface';

@Component({
  selector: 'app-register',
  imports: [CommonModule, MaterialModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: true,
})
export class RegisterComponent implements OnDestroy {
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private destroy$ = new Subject<void>();

  public onError = false;

  public form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    firstName: [
      '',
      [Validators.required, Validators.min(3), Validators.max(20)],
    ],
    lastName: [
      '',
      [Validators.required, Validators.min(3), Validators.max(20)],
    ],
    password: [
      '',
      [Validators.required, Validators.min(3), Validators.max(40)],
    ],
  });

  public submit(): void {
    const registerRequest = this.form.value as RegisterRequest;
    this.authService
      .register(registerRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (): void => {
          this.router.navigate(['/login']);
        },
        error: (): void => {
          this.onError = true;
        },
      });
  }

  public ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
