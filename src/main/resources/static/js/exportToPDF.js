let downloadPDFButton = document.getElementById('download-pdf');

downloadPDFButton.addEventListener('click', () => {

    const element = document.querySelector('.transaction-details-card');

    const options = {
        margin:       0.5,
        filename:     'transaction_receipt.pdf',
        image:        { type: 'jpeg', quality: 0.98 },
        html2canvas:  { scale: 2, useCORS: true, scrollY: 0  },
        jsPDF:        { unit: 'in', format: 'a4', orientation: 'portrait' }
    };

    return html2pdf().from(element).set(options).save();
}); 