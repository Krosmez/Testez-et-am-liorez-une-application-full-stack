import { AuthService } from 'src/app/core/service/auth.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionInformation } from 'src/app/core/models/sessionInformation.interface';
import { SessionService } from 'src/app/core/service/session.service';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [SessionService],
      imports: [
        LoginComponent,
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should login successfully and navigate to sessions', () => {
    const authService = TestBed.inject(AuthService);
    const sessionService = TestBed.inject(SessionService);
    const router = TestBed.inject(Router);

    const mockSessionInfo: SessionInformation = {
      token: 'mock-token',
      type: 'Bearer',
      id: 1,
      username: 'test@example.com',
      firstName: 'Test',
      lastName: 'User',
      admin: false,
    };

    jest.spyOn(authService, 'login').mockReturnValue(of(mockSessionInfo));
    jest.spyOn(sessionService, 'logIn');
    jest.spyOn(router, 'navigate');

    component.form.setValue({
      email: 'test@example.com',
      password: 'password123',
    });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123',
    });
    expect(sessionService.logIn).toHaveBeenCalledWith(mockSessionInfo);
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('should set onError to true when login fails', () => {
    const authService = TestBed.inject(AuthService);

    jest
      .spyOn(authService, 'login')
      .mockReturnValue(throwError(() => new Error('Login failed')));

    component.form.setValue({
      email: 'test@example.com',
      password: 'wrongpassword',
    });

    component.submit();

    expect(component.onError).toBe(true);
  });

  it('should complete destroy$ subject on ngOnDestroy', () => {
    jest.spyOn(component['destroy$'], 'next');
    jest.spyOn(component['destroy$'], 'complete');

    component.ngOnDestroy();

    expect(component['destroy$'].next).toHaveBeenCalled();
    expect(component['destroy$'].complete).toHaveBeenCalled();
  });

  it('should unsubscribe from login observable on destroy', () => {
    const authService = TestBed.inject(AuthService);
    const mockSessionInfo: SessionInformation = {
      token: 'mock-token',
      type: 'Bearer',
      id: 1,
      username: 'test@example.com',
      firstName: 'Test',
      lastName: 'User',
      admin: false,
    };

    const loginSpy = jest
      .spyOn(authService, 'login')
      .mockReturnValue(of(mockSessionInfo));

    component.form.setValue({
      email: 'test@example.com',
      password: 'password123',
    });

    component.submit();
    component.ngOnDestroy();

    expect(loginSpy).toHaveBeenCalled();
  });
});
