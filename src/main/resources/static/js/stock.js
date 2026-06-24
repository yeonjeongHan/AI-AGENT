let inventoryChartInstance = null;

document.addEventListener('DOMContentLoaded', () => {
  initYearMonthSelects();
});

function initYearMonthSelects() {
  const fromYear = document.getElementById('fromYear');
  const toYear = document.getElementById('toYear');
  const fromMonth = document.getElementById('fromMonth');
  const toMonth = document.getElementById('toMonth');

  const currentYear = new Date().getFullYear();

  // 연도: 현재년도 기준 +- 5년
  for (let y = currentYear - 5; y <= currentYear + 1; y++) {
    const opt1 = document.createElement('option');
    opt1.value = y;
    opt1.textContent = `${y}년`;
    fromYear.appendChild(opt1.cloneNode(true));
    toYear.appendChild(opt1);
  }

  // 월
  for (let m = 1; m <= 12; m++) {
    const monthValue = String(m).padStart(2, '0');
    const opt = document.createElement('option');
    opt.value = monthValue;
    opt.textContent = `${monthValue}월`;
    fromMonth.appendChild(opt.cloneNode(true));
    toMonth.appendChild(opt);
  }

  fromYear.value = currentYear;
  toYear.value = currentYear;
  fromMonth.value = '01';
  toMonth.value = String(new Date().getMonth() + 1).padStart(2, '0');
}

async function searchInventory() {
  const fromYear = document.getElementById('fromYear').value;
  const fromMonth = document.getElementById('fromMonth').value;
  const toYear = document.getElementById('toYear').value;
  const toMonth = document.getElementById('toMonth').value;

  const from = `${fromYear}-${fromMonth}`;
  const to = `${toYear}-${toMonth}`;

  if (from > to) {
    document.getElementById('result').textContent = 'From 기간이 To 기간보다 클 수 없습니다.';
    return;
  }

  document.getElementById('result').textContent = '조회 중...';

  try {
    const response = await fetch('/api/inventory/chart', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ from, to })
    });

    const data = await response.json();

    if (!response.ok) {
      document.getElementById('result').textContent =
        `에러 발생 (${response.status})\n${JSON.stringify(data, null, 2)}`;
      return;
    }

    document.getElementById('result').textContent =
      `조회 완료\n${JSON.stringify(data, null, 2)}`;

    renderInventoryChart(data);
  } catch (error) {
    document.getElementById('result').textContent = `네트워크 오류\n${error.message}`;
  }
}

function renderInventoryChart(data) {
  const ctx = document.getElementById('inventoryChart').getContext('2d');

  const labels = data.labels || [];
  const values = data.values || [];

  if (inventoryChartInstance) {
    inventoryChartInstance.destroy();
  }

  inventoryChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels,
      datasets: [{
        label: '재고 수량',
        data: values,
        backgroundColor: 'rgba(37, 99, 235, 0.75)',
        borderColor: '#60a5fa',
        borderWidth: 1,
        borderRadius: 8
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          labels: {
            color: '#e5e7eb'
          }
        }
      },
      scales: {
        x: {
          ticks: { color: '#cbd5e1' },
          grid: { color: 'rgba(148, 163, 184, 0.12)' }
        },
        y: {
          ticks: { color: '#cbd5e1' },
          grid: { color: 'rgba(148, 163, 184, 0.12)' }
        }
      }
    }
  });
}
