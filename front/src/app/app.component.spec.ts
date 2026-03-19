import { HttpClientModule } from '@angular/common/http';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionService } from 'src/app/core/service/session.service';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppComponent,
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule,
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should call sessionService.logOut and navigate to home on logout', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    const sessionService = TestBed.inject(SessionService);
    const router = TestBed.inject(Router);

    const logOutSpy = jest.spyOn(sessionService, 'logOut');
    const navigateSpy = jest.spyOn(router, 'navigate');

    app.logout();

    expect(logOutSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  });
});
