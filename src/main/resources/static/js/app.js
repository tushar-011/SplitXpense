function ensureResultModal() {

    if (document.getElementById("resultModal")) {
        return;
    }

    const modal = document.createElement("div");

    modal.id = "resultModal";
    modal.className = "modal-overlay";

    modal.innerHTML = `
        <div class="modal-box">

            <div class="modal-header">

                <h3 id="modalTitle">
                    Result
                </h3>

                <button
                    class="modal-close"
                    onclick="closeResultModal()">

                    &times;

                </button>

            </div>

            <div
                id="modalContent"
                class="modal-content">
            </div>

            <div class="modal-footer">

                <button
                    onclick="closeResultModal()">

                    Close

                </button>

            </div>

        </div>
    `;

    document.body.appendChild(modal);
}


function closeResultModal() {

    const modal =
        document.getElementById("resultModal");

    if (modal) {
        modal.style.display = "none";
    }
}


function showResultModal(title, data) {

    ensureResultModal();

    document.getElementById(
        "modalTitle"
    ).innerText = title;


    const content =
        document.getElementById(
            "modalContent"
        );


    content.innerHTML = "";


    if (typeof data === "string") {

        const paragraph =
            document.createElement("p");

        paragraph.innerText = data;

        content.appendChild(paragraph);

    } else {

        renderData(
            content,
            data
        );
    }


    document.getElementById(
        "resultModal"
    ).style.display = "flex";
}


function renderData(container, data) {

    if (Array.isArray(data)) {

        if (data.length === 0) {

            container.innerHTML =
                "<p>No records found.</p>";

            return;
        }


        if (typeof data[0] === "string") {

            const list =
                document.createElement("ul");

            data.forEach(item => {

                const li =
                    document.createElement("li");

                li.innerText = item;

                list.appendChild(li);

            });

            container.appendChild(list);

            return;
        }


        const table =
            createObjectTable(data);

        container.appendChild(table);

        return;
    }


    if (data !== null
            && typeof data === "object") {

        const entries =
            Object.entries(data);


        const table =
            document.createElement("table");


        const tbody =
            document.createElement("tbody");


        entries.forEach(([key, value]) => {

            const row =
                document.createElement("tr");


            const keyCell =
                document.createElement("th");

            keyCell.innerText =
                formatLabel(key);


            const valueCell =
                document.createElement("td");


            if (value !== null
                    && typeof value === "object") {

                valueCell.innerText =
                    readableValue(value);

            } else {

                valueCell.innerText =
                    value ?? "-";
            }


            row.appendChild(keyCell);
            row.appendChild(valueCell);

            tbody.appendChild(row);

        });


        table.appendChild(tbody);

        container.appendChild(table);

        return;
    }


    container.innerText = data;
}


function createObjectTable(items) {

    const table =
        document.createElement("table");


    const primitiveKeys =
        Object.keys(items[0]).filter(
            key =>
                items[0][key] === null
                ||
                typeof items[0][key]
                    !== "object"
        );


    const thead =
        document.createElement("thead");

    const headingRow =
        document.createElement("tr");


    primitiveKeys.forEach(key => {

        const th =
            document.createElement("th");

        th.innerText =
            formatLabel(key);

        headingRow.appendChild(th);

    });


    thead.appendChild(headingRow);

    table.appendChild(thead);


    const tbody =
        document.createElement("tbody");


    items.forEach(item => {

        const row =
            document.createElement("tr");


        primitiveKeys.forEach(key => {

            const td =
                document.createElement("td");

            td.innerText =
                readableValue(item[key]);

            row.appendChild(td);

        });


        tbody.appendChild(row);

    });


    table.appendChild(tbody);

    return table;
}


function readableValue(value) {

    if (value === null
            || value === undefined) {

        return "-";
    }


    if (typeof value !== "object") {
        return value;
    }


    if (value.name) {
        return value.name;
    }


    if (value.email) {
        return value.email;
    }


    return JSON.stringify(value);
}


function formatLabel(text) {

    return text
        .replace(/([A-Z])/g, " $1")
        .replace(/^./,
            char => char.toUpperCase());
}


async function fetchAndShow(
        url,
        title) {

    try {

        const response =
            await fetch(url);


        const text =
            await response.text();


        let data;


        try {

            data =
                JSON.parse(text);

        } catch {

            data = text;
        }


        showResultModal(
            title,
            data
        );


        return data;

    } catch {

        showResultModal(
            "Error",
            "Unable to complete the request."
        );
    }
}


async function submitFormAndShow(
        form,
        title,
        callback) {

    const formData =
        new FormData(form);


    const body =
        new URLSearchParams();


    formData.forEach(
        (value, key) => {

            body.append(
                key,
                value
            );

        }
    );


    try {

        const response =
            await fetch(
                form.action,
                {
                    method:
                        form.method
                        .toUpperCase(),

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body: body
                }
            );


        const text =
            await response.text();


        let data;


        try {

            data =
                JSON.parse(text);

        } catch {

            data = text;
        }


        showResultModal(
            title,
            data
        );


        if (callback) {
            callback(data);
        }


        return data;

    } catch {

        showResultModal(
            "Error",
            "Unable to complete the request."
        );
    }
}


document.addEventListener(
    "keydown",
    function(event) {

        if (event.key === "Escape") {
            closeResultModal();
        }

    }
);