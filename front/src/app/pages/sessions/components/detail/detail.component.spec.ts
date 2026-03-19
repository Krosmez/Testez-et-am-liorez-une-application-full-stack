import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../core/service/session.service';

import { DetailComponent } from './detail.component';
import { SessionApiService } from 'src/app/core/service/session-api.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let service: SessionService;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DetailComponent,
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
      ],
      providers: [{ provide: SessionService, useValue: mockSessionService }],
    }).compileComponents();
    service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should delete session and navigate to sessions list', (done) => {
    const sessionApiService = TestBed.inject(SessionApiService);
    const matSnackBar = TestBed.inject(MatSnackBar);
    const router = TestBed.inject(Router);

    jest.spyOn(sessionApiService, 'delete').mockReturnValue(of(undefined));
    jest.spyOn(matSnackBar, 'open');
    jest.spyOn(router, 'navigate');

    component.delete();

    expect(sessionApiService.delete).toHaveBeenCalledWith(component.sessionId);
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    done();
  });

  it('should participate in session and refresh session data', (done) => {
    const sessionApiService = TestBed.inject(SessionApiService);

    jest.spyOn(sessionApiService, 'participate').mockReturnValue(of(undefined));
    jest.spyOn(component as any, 'fetchSession');

    component.participate();

    expect(sessionApiService.participate).toHaveBeenCalledWith(
      component.sessionId,
      component.userId,
    );
    expect((component as any).fetchSession).toHaveBeenCalled();
    done();
  });

  it('should unparticipate from session and refresh session data', (done) => {
    const sessionApiService = TestBed.inject(SessionApiService);

    jest
      .spyOn(sessionApiService, 'unParticipate')
      .mockReturnValue(of(undefined));
    jest.spyOn(component as any, 'fetchSession');

    component.unParticipate();

    expect(sessionApiService.unParticipate).toHaveBeenCalledWith(
      component.sessionId,
      component.userId,
    );
    expect((component as any).fetchSession).toHaveBeenCalled();
    done();
  });

  it('should call back method and navigate back', () => {
    jest.spyOn(window.history, 'back');

    component.back();

    expect(window.history.back).toHaveBeenCalled();
  });

  it('should fetch session details and teacher on ngOnInit', (done) => {
    const sessionApiService = TestBed.inject(SessionApiService);

    const mockSession = {
      id: 1,
      name: 'Test Session',
      users: [1, 2],
      teacher_id: 1,
      date: new Date(),
      description: 'Test',
    };

    const mockTeacher = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));
    jest.spyOn(component as any, 'fetchSession').mockImplementation(() => {
      component.session = mockSession;
      component.isParticipate = true;
      component.teacher = mockTeacher;
    });

    component.ngOnInit();

    expect((component as any).fetchSession).toHaveBeenCalled();
    done();
  });

  it('should unsubscribe on ngOnDestroy', () => {
    const destroySpy = jest.spyOn((component as any).destroy$, 'next');
    const completeSpy = jest.spyOn((component as any).destroy$, 'complete');

    component.ngOnDestroy();

    expect(destroySpy).toHaveBeenCalled();
    expect(completeSpy).toHaveBeenCalled();
  });

  it('should initialize component properties in constructor', () => {
    expect(component.sessionId).toBeDefined();
    expect(component.isAdmin).toBe(true);
    expect(component.userId).toBe('1');
  });
});
