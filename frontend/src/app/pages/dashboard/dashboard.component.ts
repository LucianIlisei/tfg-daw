import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

declare const bootstrap: any;

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule]
})
export class DashboardComponent implements OnInit {
  username: string = '';
  totalBalance: number = 0;
  totalIngresos: number = 0;
  totalGastos: number = 0;

  tipoMovimiento: 'INGRESO' | 'GASTO' = 'INGRESO';
  movimientoForm: FormGroup;

  categorias: string[] = [
    'Ocio', 'Trabajo', 'Transporte', 'Comida', 'Salud',
    'Hogar', 'Educación', 'Regalos', 'Tecnología', 'Deportes',
    'Mascotas', 'Otros'
  ];

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
    this.movimientoForm = this.fb.group({
      amount: [0, [Validators.required, Validators.min(0.01)]],
      description: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.username = localStorage.getItem('username') || 'usuario';
    this.cargarTransacciones();
  }

  cargarTransacciones() {
    this.http.get<any[]>('http://localhost:8080/api/transactions', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    }).subscribe(transacciones => {
      this.totalIngresos = 0;
      this.totalGastos = 0;
      this.totalBalance = 0;

      transacciones.forEach(tx => {
        if (tx.type === 'INGRESO') {
          this.totalIngresos += tx.amount;
          this.totalBalance += tx.amount;
        } else {
          this.totalGastos += tx.amount;
          this.totalBalance -= tx.amount;
        }
      });
    });
  }

  abrirModal(tipo: 'INGRESO' | 'GASTO') {
    this.tipoMovimiento = tipo;
    this.movimientoForm.reset();
    const modal = new bootstrap.Modal(document.getElementById('modalMovimiento'));
    modal.show();
  }

  guardarMovimiento() {
    const movimiento = {
      ...this.movimientoForm.value,
      type: this.tipoMovimiento
    };

    const token = localStorage.getItem('token');

    this.http.post('http://localhost:8080/api/transactions', movimiento, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).subscribe({
      next: () => {
        Swal.fire('¡Éxito!', `${this.tipoMovimiento} añadido correctamente.`, 'success');
        bootstrap.Modal.getInstance(document.getElementById('modalMovimiento'))?.hide();
        this.cargarTransacciones();
      },
      error: (err) => {
        console.error('Error al guardar el movimiento:', err);
        Swal.fire('Error', 'No se pudo guardar el movimiento.', 'error');
      }
    });
  }

  logout() {
    Swal.fire({
      title: '¿Cerrar sesión?',
      text: 'Se cerrará tu sesión actual.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, cerrar sesión',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33'
    }).then(result => {
      if (result.isConfirmed) {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('name');
        this.router.navigate(['/login']);
      }
    });
  }

  formatEuro(value: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  }
}
