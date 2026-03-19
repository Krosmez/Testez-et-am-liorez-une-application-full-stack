import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import { AuthService } from 'src/app/core/service/auth.service';
import { of, Subject, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent,
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call authService.register with form values on submit', () => {
    const authService = TestBed.inject(AuthService);
    const registerSpy = jest
      .spyOn(authService, 'register')
      .mockReturnValue(of(undefined));

    component.form.patchValue({
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123',
    });

    component.submit();

    expect(registerSpy).toHaveBeenCalledWith({
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123',
    });
  });

  it('should navigate to /login on successful registration', () => {
    const authService = TestBed.inject(AuthService);
    const router = TestBed.inject(Router);
    jest.spyOn(authService, 'register').mockReturnValue(of(undefined));
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.submit();

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should set onError to true when registration fails', () => {
    const authService = TestBed.inject(AuthService);
    jest
      .spyOn(authService, 'register')
      .mockReturnValue(throwError(() => new Error('Registration failed')));

    component.onError = false;
    component.submit();

    expect(component.onError).toBe(true);
  });

  it('should call next and complete on destroy$ subject when ngOnDestroy is called', () => {
    const nextSpy = jest.spyOn(component['destroy$'], 'next');
    const completeSpy = jest.spyOn(component['destroy$'], 'complete');

    component.ngOnDestroy();

    expect(nextSpy).toHaveBeenCalled();
    expect(completeSpy).toHaveBeenCalled();
  });

  it('should unsubscribe from ongoing subscriptions on destroy', () => {
    const authService = TestBed.inject(AuthService);
    const registerSubject = new Subject<void>();
    jest
      .spyOn(authService, 'register')
      .mockReturnValue(registerSubject.asObservable());

    component.form.patchValue({
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123',
    });

    component.submit();
    component.ngOnDestroy();

    // After destroy, the subscription should not react
    const router = TestBed.inject(Router);
    const navigateSpy = jest.spyOn(router, 'navigate');

    registerSubject.next();

    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
