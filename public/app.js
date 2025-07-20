// app.js

document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

function initApp() {
    // Cache DOM elements
    const tabs = {
        requestTab: document.getElementById('requestTab'),
        statusTab: document.getElementById('statusTab'),
        requestPage: document.getElementById('requestPage'),
        statusPage: document.getElementById('statusPage'),
    };

    const requestPageElements = {
        executionTypeRadios: document.querySelectorAll('input[name="executionType"]'),
        scheduleFields: document.getElementById('scheduleFields'),
        scheduleDate: document.querySelector('input[name="scheduleDate"]'),
        scheduleHour: document.querySelector('input[name="scheduleHour"]'),
        scheduleMinute: document.querySelector('input[name="scheduleMinute"]'),
        scheduleSecond: document.querySelector('input[name="scheduleSecond"]'),
        addHeaderBtn: document.getElementById('addHeader'),
        headersTbody: document.getElementById('headerInfos'),
        codeElement: document.querySelector('.editable-code'),
        bodyInput: document.getElementById('bodyInput'),
        submitButton: document.getElementById('submitButton'),
        requestForm: document.getElementById('requestForm'),
        responseBody: document.getElementById('responseBody'),
        responseHeadersBody: document.getElementById('responseHeadersBody'),
    };

    let currentPage = 1;
    let recordsPerPage = 5;
    let showFavoritesOnly = false;
    let totalJobs = 0;
    const userId = generateUserId();
    console.log("Current User ID:", userId);

    function generateUserId() {
        return 'user-' + Math.random().toString(36).slice(2, 11);
    }

    // Switch between Request and Status tab
    function switchTab(tab) {
        tabs.requestPage.classList.toggle('hidden', tab !== 'request');
        tabs.statusPage.classList.toggle('hidden', tab !== 'status');

        // Update tab styles
        tabs.requestTab.classList.toggle('bg-blue-500', tab === 'request');
        tabs.requestTab.classList.toggle('text-white', tab === 'request');
        tabs.statusTab.classList.toggle('bg-blue-500', tab === 'status');
        tabs.statusTab.classList.toggle('text-white', tab === 'status');

        tabs.requestTab.classList.toggle('bg-gray-300', tab !== 'request');
        tabs.statusTab.classList.toggle('bg-gray-300', tab !== 'status');

        if (tab === 'status') {
            currentPage = 1;
            recordsPerPage = 5;
            showFavoritesOnly = false;
            totalJobs = 0;
            fetchAndRenderJobs();
        } else {
            clearResponse();
        }
    }

    // Initialize event listeners
    tabs.requestTab.addEventListener('click', () => switchTab('request'));
    tabs.statusTab.addEventListener('click', () => switchTab('status'));


    requestPageElements.addHeaderBtn.addEventListener('click', () => addHeaderRow(requestPageElements.headersTbody));
    requestPageElements.codeElement.addEventListener('blur', () => updateHiddenBodyInput(requestPageElements));
    requestPageElements.executionTypeRadios.forEach(radio => {
        radio.addEventListener('change', () => handleExecutionTypeChange(radio));
    });

    requestPageElements.requestForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await submitJob(requestPageElements);
    });

    requestPageElements.scheduleDate.addEventListener('change', () => {
        if (requestPageElements.scheduleDate.value) {
            requestPageElements.scheduleDate.style.color = "#000";
        }
    })

    function clearResponse() {
        requestPageElements.responseBody.textContent = '';
        requestPageElements.responseHeadersBody.innerHTML = '';
    }

    // Handle execution type change (now/schedule)
    function handleExecutionTypeChange(radio) {
        const {scheduleFields, scheduleDate, scheduleHour, scheduleMinute, scheduleSecond} = requestPageElements;

        if (!scheduleDate || !scheduleHour || !scheduleMinute || !scheduleSecond) return;

        const inputs = [scheduleDate, scheduleHour, scheduleMinute, scheduleSecond];

        if (radio.value === 'now') {
            scheduleFields.classList.add('hidden');
            disableFields(inputs);
        } else {
            if (!requestPageElements.scheduleDate.value) {
                requestPageElements.scheduleDate.style.color = "#9ca3af";
            }
            scheduleFields.classList.remove('hidden');
            enableFields(inputs);

            const scheduledTime = buildScheduledTime(scheduleDate, scheduleHour, scheduleMinute, scheduleSecond);
            const scheduledTimeInput = document.querySelector('input[name="scheduledTime"]');
            if (scheduledTimeInput) {
                scheduledTimeInput.value = scheduledTime;
            }
        }
    }

    // Build scheduled time string
    function buildScheduledTime(dateInput, hourInput, minuteInput, secondInput) {
        const date = dateInput?.value || '2024-01-01';
        const hour = (hourInput?.value || '').padStart(2, '0');
        const minute = (minuteInput?.value || '').padStart(2, '0');
        const second = (secondInput?.value || '').padStart(2, '0');

        return `${date}T${hour}:${minute}:${second}`;
    }

    // Disable fields and remove required
    function disableFields(inputs) {
        inputs.forEach(input => {
            if (input) {
                input.value = '';
                input.disabled = true;
                input.removeAttribute('required');
            }
        });
    }

    // Enable fields and set required
    function enableFields(inputs) {
        inputs.forEach(input => {
            if (input) {
                input.disabled = false;
                input.setAttribute('required', '');
            }
        });
    }

    // Add a new header row
    function addHeaderRow(headersTbody) {
        const newRow = document.createElement('tr');
        newRow.innerHTML = `
      <td><input type="text" name="key" class="w-full p-2" placeholder="Enter Key"></td>
      <td><input type="text" name="value" class="w-full p-2" placeholder="Enter Value"></td>
    `;
        headersTbody.appendChild(newRow);
    }

    function updateHiddenBodyInput({codeElement, bodyInput}) {
        const inputText = codeElement.textContent.trim();
        bodyInput.value = inputText;
        try {
            const parsedJSON = JSON.parse(inputText);
            const formattedJSON = JSON.stringify(parsedJSON, null, 2);
            if (codeElement.textContent !== formattedJSON) {
                codeElement.textContent = formattedJSON;
                Prism.highlightAll();
            }
        } catch (error) {
        }
    }

    // Submit the job to backend
    async function submitJob({requestForm, responseBody, responseHeadersBody}) {
        if (!requestForm.checkValidity()) {
            requestForm.reportValidity();
            return;
        }

        const formData = new FormData(requestForm);
        let apiUrl = formData.get('apiUrl').trim();

        if (apiUrl && !/^https?:\/\//i.test(apiUrl)) {
            apiUrl = 'http://' + apiUrl;
        }


        const executionType = formData.get('executionType');

        const headerRows = document.querySelectorAll('#headerInfos tr');
        const headerInfos = [];

        headerRows.forEach(row => {
            const keyInput = row.querySelector('input[name="key"]');
            const valueInput = row.querySelector('input[name="value"]');
            const key = keyInput?.value.trim();
            const value = valueInput?.value.trim();
            if (key && value) {
                headerInfos.push({key, value});
            }
        });

        let scheduledTime = null;
        if (executionType === 'schedule') {
            const scheduleDate = document.querySelector('input[name="scheduleDate"]');
            const scheduleHour = document.querySelector('input[name="scheduleHour"]');
            const scheduleMinute = document.querySelector('input[name="scheduleMinute"]');
            const scheduleSecond = document.querySelector('input[name="scheduleSecond"]');

            scheduledTime = buildScheduledTime(scheduleDate, scheduleHour, scheduleMinute, scheduleSecond);
        }
        const job = {
            apiUrl: apiUrl,
            name: formData.get('name'),
            method: formData.get('method'),
            headerInfos,
            body: formData.get('body') || '{}',
            submitted: new Date().toISOString(),
            executionTime: executionType === 'now' ? new Date().toISOString() : null,
            scheduledTime: scheduledTime,
            favorite: false,
            executionType,
            createdBy: userId
        };

        try {
            const res = await fetch('/jobs', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(job)
            });

            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);

            clearResponse();
            if (executionType === 'now') {
                const text = await res.text();
                let formattedJson = text;
                try {
                    const json = JSON.parse(text);
                    formattedJson = JSON.stringify(json.body, null, 2);
                } catch (e) {
                    console.error('Error parsing JSON:', e);
                }
                responseBody.textContent = formattedJson;

                Prism.highlightAll();
                responseHeadersBody.innerHTML = [...res.headers].map(([key, value]) => `
              <tr>
                <td class="p-2 border border-gray-300">${key}</td>
                <td class="p-2 border border-gray-300">${value}</td>
              </tr>
            `).join('');
            }

            requestForm.reset();

            // Reset contenteditable and scheduled fields
            if (requestPageElements.codeElement) {
                requestPageElements.codeElement.textContent = '';
                Prism.highlightElement(requestPageElements.codeElement);
            }

            if (requestPageElements.bodyInput) {
                requestPageElements.bodyInput.value = '';
            }

            if (requestPageElements.scheduleFields) {
                const {
                    scheduleDate,
                    scheduleHour,
                    scheduleMinute,
                    scheduleSecond
                } = requestPageElements;

                if (scheduleDate || scheduleHour || scheduleMinute || scheduleSecond) {
                    const inputs = [scheduleDate, scheduleHour, scheduleMinute, scheduleSecond];
                    disableFields(inputs)
                }
                requestPageElements.scheduleFields.classList.add('hidden');
            }

            document.querySelectorAll('input[name="executionType"]').forEach(radio => {
                radio.checked = radio.value === 'now';
            });

        } catch (error) {
            console.error('[Submit Error]', error);
            alert(`Failed to submit job: ${error.message}`);
        }
    }

    // status page
    const statusPageElements = {
        showFavoritesBtn: document.getElementById('showFavorites'),
        recordsPerPageSelect: document.getElementById('recordsPerPage'),
        prevPageBtn: document.getElementById('prevPage'),
        nextPageBtn: document.getElementById('nextPage'),
        pageInfo: document.getElementById('pageInfo'),
        jobList: document.getElementById('jobList')
    };


    statusPageElements.showFavoritesBtn.addEventListener('click', () => toggleFavorites(statusPageElements));
    statusPageElements.recordsPerPageSelect.addEventListener('change', (e) => {
        recordsPerPage = parseInt(e.target.value);
        currentPage = 1;
        fetchAndRenderJobs();
    });

    statusPageElements.prevPageBtn.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            fetchAndRenderJobs();
        }
    });

    statusPageElements.nextPageBtn.addEventListener('click', () => {
        if ((currentPage * recordsPerPage) < totalJobs) {
            currentPage++;
            fetchAndRenderJobs();
        }
    });


    async function fetchJobs() {
        try {
            const url = `/jobs?page=${currentPage - 1}&size=${recordsPerPage}&favorite=${showFavoritesOnly}&createdBy=${userId}`;
            const res = await fetch(url, {method: 'GET', headers: {Accept: 'application/json'}});
            if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}, Text: ${res.statusText}`);
            const data = await res.json();
            totalJobs = data.totalElements;
            return data.content || [];
        } catch (error) {
            console.error('[Load Jobs Error]', error);
            alert('Failed to load jobs. Please try again later.');
            return [];
        }
    }


    // Render the job list in table
    function renderJobsUI(jobs) {

        statusPageElements.jobList.innerHTML = '';
        jobs.forEach(job => {
            const row = document.createElement('tr');
            row.innerHTML = `
          <td class="p-2"><span class="star ${job.favorite ? 'filled' : 'unfilled'}" data-id="${job.id}">★</span></td>
          <td class="p-2">${job.createdBy || '-'}</td>
          <td class="p-2">${job.name || '-'}</td>
          <td class="p-2">${formatDate(job.submitted)}</td>
          <td class="p-2">${formatDate(job.executionTime)}</td>
          <td class="p-2 ${getStatusColorClass(job.status)}">${job.status || '-'}</td>
          <td class="p-2">
            <button class="details-btn px-2 py-1 bg-blue-500 text-white rounded" data-id="${job.id}">Details</button>
          </td>
        `;
            statusPageElements.jobList.appendChild(row);
        });

        if (statusPageElements.recordsPerPageSelect) {
            statusPageElements.recordsPerPageSelect.innerHTML = '';
            for (let i = 1; i <= totalJobs; i++) {
                const opt = document.createElement('option');
                opt.value = i;
                opt.textContent = i;
                if (i <= recordsPerPage) opt.selected = true;
                statusPageElements.recordsPerPageSelect.appendChild(opt);
            }
        }

        const startIdx = (currentPage - 1) * recordsPerPage + 1;
        const end = currentPage * recordsPerPage;
        const endIdx = Math.min(end, totalJobs);
        statusPageElements.pageInfo.textContent = `${startIdx}-${endIdx} of ${totalJobs}`;

        statusPageElements.prevPageBtn.disabled = currentPage === 1;
        statusPageElements.nextPageBtn.disabled = end >= totalJobs;
        statusPageElements.prevPageBtn.classList.toggle('text-gray-400', currentPage === 1);
        statusPageElements.nextPageBtn.classList.toggle('text-gray-400', end >= totalJobs);

        bindStarClick(jobs);
    }


    async function fetchAndRenderJobs() {
        fetchJobs()
            .then(
                jobs => renderJobsUI(jobs));
    }


    function getStatusColorClass(status) {
        if (status === 'Completed') return 'text-green-500';
        return 'text-orange-500';
    }

    function formatDate(date) {
        if (!date) return '-';
        const d = new Date(date);
        const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        const month = monthNames[d.getMonth()];
        const day = String(d.getDate());
        const year = d.getFullYear();
        return `${month} ${day}, ${year}`;
    }

    function toggleFavorites({showFavoritesBtn}) {
        showFavoritesOnly = !showFavoritesOnly;
        showFavoritesBtn.classList.toggle('filled', showFavoritesOnly);
        currentPage = 1;
        fetchAndRenderJobs();
    }

    function bindStarClick(jobs) {
        document.querySelectorAll('td .star').forEach(star => {
            star.addEventListener('click', () => {
                const id = parseInt(star.getAttribute('data-id'));
                const job = jobs.find(j => j.id === id);
                if (job) {
                    const newFavoriteStatus = !job.favorite;
                    fetch(`/jobs/${id}`, {
                        method: 'PUT',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({favorite: newFavoriteStatus, createdBy: userId})
                    })
                        .then(res => {
                            if (!res.ok) throw new Error('Failed to update favorite');
                            currentPage = 1;
                            fetchAndRenderJobs();
                        })
                        .catch(err => {
                            console.error('[Update Favorite Error]', err);
                            fetchAndRenderJobs();
                        });
                }
            });
        });
    }
}
