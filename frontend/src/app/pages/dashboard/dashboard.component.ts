// Importaciones de Angular y librerías externas necesarias
import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ChartType, ChartConfiguration } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import Swal from 'sweetalert2';

declare const bootstrap: any; // Importamos Bootstrap JS para manejar modales manualmente

// Decorador del componente con configuración standalone
@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, NgChartsModule]
})
export class DashboardComponent implements OnInit {
  // ==================== Usuario y estado financiero ====================
  username: string = '';
  totalBalance: number = 0;
  totalIngresos: number = 0;
  totalGastos: number = 0;
  transacciones: any[] = [];
  mostrarTodas: boolean = false;

  // ==================== Añadir movimiento ====================
  tipoMovimiento: 'INGRESO' | 'GASTO' = 'INGRESO';
  movimientoForm: FormGroup;

  categoriasIngreso: string[] = ['Nómina', 'Trabajo freelance', 'Venta', 'Devolución', 'Regalo', 'Otros'];
  categoriasGasto: string[] = [
    'Ocio', 'Trabajo', 'Transporte', 'Comida', 'Salud',
    'Hogar', 'Educación', 'Regalos', 'Tecnología', 'Deportes',
    'Mascotas', 'Otros'
  ];
  categorias: string[] = this.categoriasGasto;

  // ==================== Análisis de gastos ====================
  analisisGastos: { categoria: string, porcentaje: number, total: number }[] = [];
  mostrarGrafico: boolean = false;

  pieChartType: 'pie' = 'pie';
  pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels: [],
    datasets: [{ data: [], backgroundColor: [] }]
  };
  pieChartOptions: ChartConfiguration<'pie'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        labels: { color: 'white' }
      }
    }
  };

  // ==================== Constructor ====================
  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
    this.movimientoForm = this.fb.group({
      amount: [0, [Validators.required, Validators.min(0.01)]],
      description: ['', Validators.required]
    });
  }

  // ==================== Lifecycle hook ====================
  ngOnInit(): void {
    this.username = localStorage.getItem('username') || 'usuario';
    this.cargarTransacciones();
  }

  // ==================== Cargar y procesar transacciones ====================
  cargarTransacciones() {
    this.http.get<any[]>('http://localhost:8080/api/transactions', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    }).subscribe(transacciones => {
      this.transacciones = transacciones.reverse();
      this.totalIngresos = 0;
      this.totalGastos = 0;
      this.totalBalance = 0;

      const categoriasSuma: { [key: string]: number } = {};

      transacciones.forEach(tx => {
        const validAmount = Math.max(0, tx.amount);

        if (tx.type === 'INGRESO') {
          this.totalIngresos += validAmount;
          this.totalBalance += validAmount;
        } else {
          this.totalGastos += validAmount;
          this.totalBalance -= validAmount;

          const categoria = tx.description;
          categoriasSuma[categoria] = (categoriasSuma[categoria] || 0) + validAmount;
        }
      });


      const totalGastosValido = Object.values(categoriasSuma).reduce((a, b) => a + b, 0);
      this.analisisGastos = Object.entries(categoriasSuma).map(([categoria, cantidad]) => ({
        categoria,
        porcentaje: totalGastosValido > 0 ? (cantidad / totalGastosValido) * 100 : 0,
        total: cantidad
      })).sort((a, b) => b.porcentaje - a.porcentaje);

      // Datos para gráfico de pastel
      this.pieChartData = {
        labels: this.analisisGastos.map(item => item.categoria),
        datasets: [{
          data: this.analisisGastos.map(item => item.total),
          backgroundColor: this.analisisGastos.map(() => `hsl(${Math.random() * 360}, 70%, 60%)`)
        }]
      };
    });
  }

  // ==================== Mostrar últimas transacciones ====================
  obtenerTransaccionesVisibles(): any[] {
    return this.mostrarTodas ? this.transacciones : this.transacciones.slice(0, 3);
  }

  toggleMostrarTodas() {
    this.mostrarTodas = !this.mostrarTodas;
  }

  // ==================== Abrir modales Bootstrap ====================
  abrirModal(tipo: 'INGRESO' | 'GASTO') {
    this.tipoMovimiento = tipo;
    this.categorias = tipo === 'INGRESO' ? this.categoriasIngreso : this.categoriasGasto;
    this.movimientoForm.reset();
    const modal = new bootstrap.Modal(document.getElementById('modalMovimiento'));
    modal.show();
  }

  abrirModalAnalisis() {
    const modal = new bootstrap.Modal(document.getElementById('modalAnalisis'));
    modal.show();
  }

  // ==================== Guardar movimiento financiero ====================
  guardarMovimiento() {
    const movimiento = {
      ...this.movimientoForm.value,
      type: this.tipoMovimiento
    };

    movimiento.amount = Math.max(0, movimiento.amount); // Seguridad básica

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

  // ==================== Cierre de sesión ====================
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

  // ==================== Formatear números a formato € ====================
  formatEuro(value: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  }
}
