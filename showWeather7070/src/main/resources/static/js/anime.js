// 城市/天数选项游动动画聚焦控制
function pauseOptionAnimation(optionEl) {
    optionEl.classList.add('active');
}
function resumeOptionAnimation(optionEl) {
    optionEl.classList.remove('active');
}

function drawConnectLines() {
    const svg = document.getElementById('connectLine');
    if (!svg) return;
    // 获取城市、天数、标题的中心坐标
    const city = document.getElementById('cityOption');
    const days = document.getElementById('daysOption');
    const title = document.getElementById('forecastTitleOnSemicircle');
    if (!city || !days || !title) return;
    const cityRect = city.getBoundingClientRect();
    const daysRect = days.getBoundingClientRect();
    const titleRect = title.getBoundingClientRect();
    // 计算svg相对页面的偏移
    const svgRect = svg.getBoundingClientRect();
    // 计算中心点
    const cityX = cityRect.left + cityRect.width/2 - svgRect.left;
    const cityY = cityRect.bottom - svgRect.top;
    const daysX = daysRect.left + daysRect.width/2 - svgRect.left;
    const daysY = daysRect.bottom - svgRect.top;
    const titleX = titleRect.left + titleRect.width/2 - svgRect.left;
    const titleY = titleRect.top + titleRect.height - svgRect.top;
    // 绘制
    svg.innerHTML = `<line x1="${cityX}" y1="${cityY}" x2="${titleX}" y2="${titleY}" stroke="var(--main-blue3)" stroke-width="4" stroke-linecap="round" opacity="0.4"/>` +
                    `<line x1="${daysX}" y1="${daysY}" x2="${titleX}" y2="${titleY}" stroke="var(--main-pink2)" stroke-width="4" stroke-linecap="round" opacity="0.4"/>`;
    // 动画帧持续更新
    window.requestAnimationFrame(drawConnectLines);
}
window.addEventListener('resize', drawConnectLines);

// 随机浮动动画
function randomFloat(min, max) {
    return Math.random() * (max - min) + min;
}
function animateOption(option) {
    let t = 0;
    option.seed = Math.random() * 100;
    function step() {
        t += 0.03 + randomFloat(-0.01, 0.01);
        const x = Math.sin(t + option.seed) * 12 + randomFloat(-2,2);
        const y = Math.cos(t + option.seed) * 8 + randomFloat(-2,2);
        option.style.transform = `translate(${x}px, ${y}px) scale(1.04)`;
        option._animId = requestAnimationFrame(step);
    }
    step();
}

document.addEventListener('DOMContentLoaded', function() {
    setTimeout(drawConnectLines, 100);
    // 只在查询界面存在时再绑定省份/城市相关事件
    if (!document.getElementById('queryView')) return;
    // 城市选项气泡弹出
    const cityOption = document.getElementById('cityOption');
    const daysOption = document.getElementById('daysOption');
    const provinceBubbleFloat = document.getElementById('provinceBubbleFloat');
    const provinceDropdown = document.getElementById('provinceDropdown');
    const provinceSearch = document.getElementById('provinceSearch');
    const provinceDropdownList = document.getElementById('provinceDropdownList');
    const cityToProvinceLine = document.getElementById('cityToProvinceLine');
    const cityBubbleFloatContainer = document.getElementById('cityBubbleFloatContainer');
    let provinceListData = window.provinceListData;

    // 选择状态
    let selectedProvince = null;
    let selectedCity = null;
    let provinceDropdownOpen = false;
    let cityDropdownOpen = false;

    // 省份下拉栏和城市下拉栏容器
    let cityDropdown = null;

    // 阻止点击省份下拉栏时冒泡到document，避免省份气泡被隐藏
    provinceDropdown.addEventListener('mousedown', function(e) {
        e.stopPropagation();
    });

    // 1. 点击城市按钮，弹出省份气泡
    cityOption.addEventListener('click', function(e) {
        // 停止城市按钮浮动动画
        cityOption.style.animationPlayState = 'paused';
        // 添加选中样式
        cityOption.classList.add('selected');
        // 重置所有状态
        selectedProvince = null;
        selectedCity = null;
        provinceDropdownOpen = false;
        cityDropdownOpen = false;
        provinceBubbleFloat.innerText = '省份';
        provinceBubbleFloat.classList.remove('selected');
        provinceBubbleFloat.style.transform = '';
        provinceBubbleFloat.style.display = 'none';
        cityBubbleFloatContainer.innerHTML = '';
        cityToProvinceLine.innerHTML = '';
        if (cityDropdown) {
            cityDropdown.remove();
            cityDropdown = null;
        }
        // 定位省份气泡
        const cityRect = cityOption.getBoundingClientRect();
        provinceBubbleFloat.style.position = 'fixed';
        provinceBubbleFloat.style.left = (cityRect.right + 200) + 'px';
        provinceBubbleFloat.style.top = cityRect.top + 'px';
        provinceBubbleFloat.style.display = 'flex';
        animateProvinceBubble(provinceBubbleFloat);
        // 调试输出
        setTimeout(function() {
            console.log('[debug] cityOption rect:', cityOption.getBoundingClientRect());
            console.log('[debug] provinceBubbleFloat rect:', provinceBubbleFloat.getBoundingClientRect());
            console.log('[debug] cityOption offsetWidth:', cityOption.offsetWidth, 'offsetHeight:', cityOption.offsetHeight);
            console.log('[debug] provinceBubbleFloat offsetWidth:', provinceBubbleFloat.offsetWidth, 'offsetHeight:', provinceBubbleFloat.offsetHeight);
        }, 100);
        // 动态连线
        function updateCityToProvinceLine() {
            if (provinceBubbleFloat.style.display === 'flex') {
                const cityRect = cityOption.getBoundingClientRect();
                const provinceRect = provinceBubbleFloat.getBoundingClientRect();
                const svg = document.getElementById('cityToProvinceLine');
                const cityX = cityRect.right;
                const cityY = cityRect.top + cityRect.height / 2;
                const provinceX = provinceRect.left;
                const provinceY = provinceRect.top + provinceRect.height / 2;
                svg.innerHTML = `<line x1="${cityX}" y1="${cityY}" x2="${provinceX}" y2="${provinceY}" stroke="var(--main-blue2)" stroke-width="4" stroke-linecap="round" opacity="0.4"/>`;
                requestAnimationFrame(updateCityToProvinceLine);
            } else {
                document.getElementById('cityToProvinceLine').innerHTML = '';
            }
        }
        updateCityToProvinceLine();
    });

    // 2. 点击省份气泡，固定并弹出省份下拉
    provinceBubbleFloat.addEventListener('click', function(e) {
        if (provinceDropdownOpen) return;
        provinceBubbleFloat.classList.add('selected');
        cancelProvinceBubbleAnimation();
        provinceDropdown.style.display = 'flex';
        provinceDropdownOpen = true;
        // 定位下拉在省份气泡正下方，使用fixed定位
        provinceDropdown.style.position = 'fixed';
        const provinceRect = provinceBubbleFloat.getBoundingClientRect();
        provinceDropdown.style.left = provinceRect.left + 'px';
        provinceDropdown.style.top = (provinceRect.bottom + 8) + 'px';
        provinceSearch.value = '';
        renderProvinceDropdownList('');
        provinceSearch.focus();
    });

    // 3. 选择省份，下拉消失，省份气泡内容变为省份名，高亮，城市下拉在城市按钮下方弹出
    provinceDropdownList.addEventListener('click', function(e) {
        if(e.target.classList.contains('province-dropdown-item')) {
            const provinceName = e.target.innerText;
            const provinceId = e.target.getAttribute('data-provinceid');
            selectedProvince = provinceId;
            provinceBubbleFloat.innerText = provinceName;
            provinceBubbleFloat.classList.add('selected');
            provinceDropdown.style.display = 'none';
            provinceDropdownOpen = false;
            // 城市下拉
            if (cityDropdown) {
                cityDropdown.remove();
            }
            cityDropdown = document.createElement('div');
            cityDropdown.className = 'city-dropdown';
            cityDropdown.style.position = 'absolute';
            // 定位在城市按钮下方
            const cityRect = cityOption.getBoundingClientRect();
            cityDropdown.style.left = cityRect.left + 'px';
            cityDropdown.style.top = (cityRect.bottom + 8) + 'px';
            // 填充城市列表
            const cityList = window.provinceCityMap[provinceId] || [];
            cityDropdown.innerHTML = `<input type="text" id="citySearch" placeholder="搜索城市" autocomplete="off"><div id="cityDropdownList"></div>`;
            document.body.appendChild(cityDropdown);
            cityDropdownOpen = true;
            // 渲染城市列表
            function renderCityDropdownList(keyword) {
                const listDiv = cityDropdown.querySelector('#cityDropdownList');
                listDiv.innerHTML = '';
                // 调试输出
                console.log('[system] typeof provinceCityMap:', typeof provinceCityMap);
                console.log('[system] provinceCityMap keys:', Object.keys(provinceCityMap));
                console.log('[system] provinceId:', provinceId, typeof provinceId);
                const cityList = provinceCityMap[provinceId] || [];
                console.log('[system] cityList:', cityList);
                cityList.filter(c => !keyword || c.city.includes(keyword)).forEach(c => {
                    const item = document.createElement('div');
                    item.className = 'city-dropdown-item';
                    item.innerText = c.city;
                    item.setAttribute('data-cityid', c.cityId);
                    listDiv.appendChild(item);
                });
            }
            renderCityDropdownList('');
            // 搜索事件
            cityDropdown.querySelector('#citySearch').addEventListener('input', function(e) {
                renderCityDropdownList(e.target.value.trim());
            });
            // 选择城市
            cityDropdown.querySelector('#cityDropdownList').addEventListener('click', function(e) {
                if(e.target.classList.contains('city-dropdown-item')) {
                    const cityName = e.target.innerText;
                    const cityId = e.target.getAttribute('data-cityid');
                    selectedCity = cityId;
                    cityOption.querySelector('span').innerText = cityName;
                    document.getElementById('cityId').value = cityId;
                    // 关闭所有气泡和下拉
                    provinceBubbleFloat.style.display = 'none';
                    provinceBubbleFloat.classList.remove('selected');
                    provinceBubbleFloat.innerText = '省份';
                    if (cityDropdown) {
                        cityDropdown.remove();
                        cityDropdown = null;
                    }
                    cityToProvinceLine.innerHTML = '';
                    // 恢复城市按钮浮动动画和移除selected样式
                    cityOption.style.animationPlayState = '';
                    cityOption.classList.remove('selected');
                }
            });
            // 城市下拉失焦
            cityDropdown.querySelector('#citySearch').addEventListener('blur', function() {
                setTimeout(function() {
                    if (cityDropdown && !cityDropdown.contains(document.activeElement)) {
                        cityDropdown.remove();
                        cityDropdown = null;
                        cityDropdownOpen = false;
                    }
                }, 150);
            });
        }
    });

    // 4. 省份下拉失焦
    provinceSearch.addEventListener('blur', function() {
        setTimeout(function() {
            if (!provinceDropdown.contains(document.activeElement)) {
                provinceDropdown.style.display = 'none';
                provinceDropdownOpen = false;
            }
        }, 150);
    });

    // 5. 点击空白处关闭所有气泡和下拉
    // 省份下拉定位（相对于.forecast-header）
    const header = document.querySelector('.forecast-header');
    // 全局点击事件只判断是否点击在.forecast-header内
    window.addEventListener('mousedown', function(e) {
        // 0. 点击城市选项时不做任何处理，仅输出system日志（刷新省份气泡已在cityOption点击事件中处理）
        if (cityOption.contains(e.target)) {
            console.log('[system] 点击城市选项后省份气泡刷新由cityOption事件处理，此处不做任何处理');
            return;
        }
        // 1. 点击天数相关选项，隐藏省份气泡
        if (daysOption.contains(e.target) || (typeof daysBubble !== 'undefined' && daysBubble.contains(e.target))) {
            console.log('[system] 省份气泡因点击天数选项而消失');
            provinceBubbleFloat.style.display = 'none';
            provinceBubbleFloat.classList.remove('selected');
            provinceBubbleFloat.innerText = '省份';
            cityBubbleFloatContainer.innerHTML = '';
            cityToProvinceLine.innerHTML = '';
            if (cityDropdown) {
                cityDropdown.remove();
                cityDropdown = null;
            }
            cityOption.style.animationPlayState = '';
            cityOption.classList.remove('selected');
            return;
        }
        // 2. 点击省份气泡、下拉栏、搜索栏、下拉项、城市下拉栏不隐藏
        if (
            provinceBubbleFloat.contains(e.target) ||
            provinceDropdown.contains(e.target) ||
            (typeof cityDropdown !== 'undefined' && cityDropdown && cityDropdown.contains(e.target))
        ) {
            console.log('[system] 点击省份气泡/下拉栏/搜索栏/城市下拉栏，省份气泡保持显示');
            return;
        }
        // 3. 其它情况（点击非部件区域），隐藏省份气泡
        console.log('[system] 省份气泡因点击非部件区域而消失');
        provinceBubbleFloat.style.display = 'none';
        provinceBubbleFloat.classList.remove('selected');
        provinceBubbleFloat.innerText = '省份';
        cityBubbleFloatContainer.innerHTML = '';
        cityToProvinceLine.innerHTML = '';
        if (cityDropdown) {
            cityDropdown.remove();
            cityDropdown = null;
        }
        cityOption.style.animationPlayState = '';
        cityOption.classList.remove('selected');
    });

    // 1. 创建天数气泡 daysBubbleFloat，使用 bubble-option 样式
    let daysBubbleFloat = document.getElementById('daysBubbleFloat');
    if (!daysBubbleFloat) {
        daysBubbleFloat = document.createElement('div');
        daysBubbleFloat.id = 'daysBubbleFloat';
        daysBubbleFloat.className = 'bubble-option'; // 统一气泡样式
        daysBubbleFloat.style.display = 'none';
        document.body.appendChild(daysBubbleFloat);
    }
    // 天数气泡连线SVG
    let daysToBubbleLine = document.getElementById('daysToBubbleLine');
    if (!daysToBubbleLine) {
        daysToBubbleLine = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        daysToBubbleLine.setAttribute('id', 'daysToBubbleLine');
        daysToBubbleLine.setAttribute('class', 'connect-line');
        daysToBubbleLine.style.position = 'absolute';
        daysToBubbleLine.style.top = '0';
        daysToBubbleLine.style.left = '0';
        daysToBubbleLine.style.width = '100vw';
        daysToBubbleLine.style.height = '100vh';
        daysToBubbleLine.style.zIndex = '1';
        daysToBubbleLine.style.pointerEvents = 'none';
        document.body.appendChild(daysToBubbleLine);
    }
    // 2. 天数选项点击弹出气泡
    if (daysOption) {
        daysOption.addEventListener('click', function() {
            const daysRect = daysOption.getBoundingClientRect();
            daysBubbleFloat.style.left = (daysRect.left + daysRect.width + 40) + 'px';
            daysBubbleFloat.style.top = daysRect.top + 'px';
            daysBubbleFloat.style.display = 'flex';
            // 渲染天数选项
            daysBubbleFloat.innerHTML = `
                <div class="bubble-days" data-days="1">1天</div>
                <div class="bubble-days" data-days="7">7天</div>
            `;
            animateProvinceBubble(daysBubbleFloat);
            // 动态连线
            function updateDaysToBubbleLine() {
                if (daysBubbleFloat.style.display === 'flex') {
                    const daysRect = daysOption.getBoundingClientRect();
                    const bubbleRect = daysBubbleFloat.getBoundingClientRect();
                    const svg = daysToBubbleLine;
                    const daysX = daysRect.right;
                    const daysY = daysRect.top + daysRect.height / 2;
                    const bubbleX = bubbleRect.left;
                    const bubbleY = bubbleRect.top + bubbleRect.height / 2;
                    svg.innerHTML = `<line x1="${daysX}" y1="${daysY}" x2="${bubbleX}" y2="${bubbleY}" stroke="var(--main-pink2)" stroke-width="4" stroke-linecap="round" opacity="0.4"/>`;
                    requestAnimationFrame(updateDaysToBubbleLine);
                } else {
                    daysToBubbleLine.innerHTML = '';
                }
            }
            updateDaysToBubbleLine();
            // 调试输出
            setTimeout(function() {
                console.log('[debug] daysOption rect:', daysOption.getBoundingClientRect());
                console.log('[debug] daysBubbleFloat rect:', daysBubbleFloat.getBoundingClientRect());
                console.log('[debug] daysOption offsetWidth:', daysOption.offsetWidth, 'offsetHeight:', daysOption.offsetHeight);
                console.log('[debug] daysBubbleFloat offsetWidth:', daysBubbleFloat.offsetWidth, 'offsetHeight:', daysBubbleFloat.offsetHeight);
            }, 100);
            console.log('[system] 显示天数气泡');
        });
        // 3. 天数气泡选项点击事件
        daysBubbleFloat.addEventListener('click', function(e) {
            if(e.target.classList.contains('bubble-days')) {
                const days = e.target.getAttribute('data-days');
                document.getElementById('type').value = days;
                document.getElementById('daysOptionText').innerText = days + '天';
                daysBubbleFloat.style.display = 'none';
                daysToBubbleLine.innerHTML = '';
                console.log('[system] 选中天数：' + days + '天');
            }
        });
        // 4. 点击空白关闭天数气泡
        document.addEventListener('mousedown', function(e) {
            if(!daysBubbleFloat.contains(e.target) && e.target !== daysOption) {
                daysBubbleFloat.style.display = 'none';
                daysToBubbleLine.innerHTML = '';
            }
        });
    }
    // 查询功能集成到半圆形
    const semicircle = document.getElementById('forecastSemicircle');
    const detail = document.getElementById('forecastDetail');
    if(semicircle) {
        semicircle.addEventListener('click', function() {
            // 直接读取select的值
            const cityId = document.getElementById('cityId').value;
            const type = document.getElementById('type').value;
            const citySelected = cityId !== '';
            const daysSelected = type === '1' || type === '7';
            if(!citySelected) {
                alert('请选择查询城市');
                console.log('[system] 查询失败：未选择城市');
                return;
            }
            if(!daysSelected) {
                alert('请选择查询天数');
                console.log('[system] 查询失败：未选择天数');
                return;
            }
            console.log(`[system] 查询：cityId=${cityId}, type=${type}`);
            window.location.href = `/getWeatherThy?city=${encodeURIComponent(cityId)}&type=${type}`;
        });
    }
    // 选项动画/交互后也重绘
    [document.getElementById('cityOption'), document.getElementById('daysOption')].forEach(function(el) {
        if (el) el.addEventListener('transitionend', drawConnectLines);
    });
    // 启动随机浮动动画
    document.querySelectorAll('.city-option, .days-option').forEach(animateOption);

    // 省份气泡和下拉联动
    // 省份气泡动画停止
    function cancelProvinceBubbleAnimation() {
        if (provinceBubbleFloat._animId) {
            cancelAnimationFrame(provinceBubbleFloat._animId);
            provinceBubbleFloat._animId = null;
        }
    }
    // 动态连线城市按钮和省份气泡
    function drawCityToProvinceLine() {
        const svg = cityToProvinceLine;
        if (!svg || provinceBubbleFloat.style.display === 'none') {
            svg.innerHTML = '';
            return;
        }
        const city = document.getElementById('cityOption');
        const cityRect = city.getBoundingClientRect();
        const provinceRect = provinceBubbleFloat.getBoundingClientRect();
        const svgRect = svg.getBoundingClientRect();
        const cityX = cityRect.right - svgRect.left;
        const cityY = cityRect.top + cityRect.height/2 - svgRect.top;
        const provinceX = provinceRect.left - svgRect.left;
        const provinceY = provinceRect.top + provinceRect.height/2 - svgRect.top;
        svg.innerHTML = `<line x1="${cityX}" y1="${cityY}" x2="${provinceX}" y2="${provinceY}" stroke="var(--main-blue2)" stroke-width="4" stroke-linecap="round" opacity="0.4"/>`;
    }
    // 每帧更新连线
    function animateCityToProvinceLine() {
        drawCityToProvinceLine();
        requestAnimationFrame(animateCityToProvinceLine);
    }
    animateCityToProvinceLine();

    // 省份气泡浮动动画
    function animateProvinceBubble(option) {
        let t = 0;
        option.seed = Math.random() * 100;
        function step() {
            t += 0.03 + randomFloat(-0.01, 0.01);
            const x = Math.sin(t + option.seed) * 12 + randomFloat(-2,2);
            const y = Math.cos(t + option.seed) * 8 + randomFloat(-2,2);
            option.style.transform = `translate(${x}px, ${y}px) scale(1.04)`;
            option._animId = requestAnimationFrame(step);
        }
        step();
    }

    // 渲染省份下拉
    function renderProvinceDropdownList(keyword) {
        let list = window.provinceListData;
        if (!Array.isArray(list)) list = [];
        list = list.filter(function(p){
            return !keyword || p.province.indexOf(keyword) !== -1;
        });
        provinceDropdownList.innerHTML = list.map(function(p) {
            return `<div class="province-dropdown-item" data-provinceid="${p.provinceId}">${p.province}</div>`;
        }).join('');
    }
    // 省份搜索事件
    provinceSearch.addEventListener('input', function() {
        renderProvinceDropdownList(this.value);
    });

    // 省份下拉项点击事件（整块可点）
    provinceDropdownList.addEventListener('mousedown', function(e) {
        let item = e.target.closest('.province-dropdown-item');
        if(item) {
            item.dispatchEvent(new Event('click', {bubbles: false}));
            e.preventDefault();
        }
    });
    // 城市下拉项点击事件（整块可点）
    document.body.addEventListener('mousedown', function(e) {
        if(cityDropdown && cityDropdown.contains(e.target)) {
            let item = e.target.closest('.province-dropdown-item');
            if(item) {
                item.dispatchEvent(new Event('click', {bubbles: false}));
                e.preventDefault();
            }
        }
    });
}); 