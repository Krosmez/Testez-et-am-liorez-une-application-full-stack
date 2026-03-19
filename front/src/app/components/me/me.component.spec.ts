import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { SessionService } from 'src/app/core/service/session.service';
import { UserService } from 'src/app/core/service/user.service';
import { expect } from '@jest/globals';
import { of } from 'rxjs';

import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
    logOut: jest.fn(),
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MeComponent,
        HttpClientModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      providers: [{ provide: SessionService, useValue: mockSessionService }],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get user information on init', () => {
    const mockUser = {
      id: 1,
      email: 'test@test.com',
      firstName: 'Test',
      lastName: 'User',
      admin: true,
      password: 'password123',
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    const userService = TestBed.inject(UserService);
    jest.spyOn(userService, 'getById').mockReturnValue(of(mockUser));

    component.ngOnInit();

    expect(userService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(mockUser);
  });

  it('should call window.history.back when back is called', () => {
    jest.spyOn(window.history, 'back').mockImplementation();

    component.back();

    expect(window.history.back).toHaveBeenCalled();
  });

  it('should unsubscribe on destroy', () => {
    const destroySpy = jest.spyOn(component['destroy$'], 'next');
    const completeSpy = jest.spyOn(component['destroy$'], 'complete');

    component.ngOnDestroy();

    expect(destroySpy).toHaveBeenCalled();
    expect(completeSpy).toHaveBeenCalled();
  });

  it('should delete user account and navigate to home', () => {
    const userService = TestBed.inject(UserService);
    const matSnackBar = TestBed.inject(MatSnackBar);
    const router = TestBed.inject(Router);

    jest.spyOn(userService, 'delete').mockReturnValue(of(undefined));
    jest.spyOn(matSnackBar, 'open');
    jest.spyOn(router, 'navigate');

    component.delete();

    expect(userService.delete).toHaveBeenCalledWith('1');
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
